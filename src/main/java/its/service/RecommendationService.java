package its.service;

import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.user.Role;
import its.domain.user.User;
import its.repository.issue.IssueRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class RecommendationService {
    private static final int DEFAULT_LIMIT = 3;
    private static final double TITLE_TERM_WEIGHT = 2.0;
    private static final double DESCRIPTION_TERM_WEIGHT = 1.0;
    private static final double SAME_PRIORITY_BONUS = 8.0;
    private static final double SIMILAR_PRIORITY_BONUS = 3.0;
    private static final double WORKLOAD_PENALTY_PER_OPEN_ISSUE = 3.0;
    private static final double MAX_WORKLOAD_PENALTY = 15.0;

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "a", "an", "and", "or", "to", "of", "in", "on", "for", "with",
            "is", "are", "was", "were", "be", "this", "that", "when", "after",
            "before", "into", "from", "page", "issue", "error", "bug",
            "이슈", "오류", "문제", "발생", "화면", "기능", "때", "후", "전"
    );

    private final IssueRepository issueRepository;

    public RecommendationService(IssueRepository issueRepository) {
        this.issueRepository = Objects.requireNonNull(issueRepository, "issueRepository must not be null");
    }

    public List<AssigneeRecommendation> recommendAssignees(Long issueId) {
        return recommendAssignees(issueId, DEFAULT_LIMIT);
    }

    public List<AssigneeRecommendation> recommendAssignees(Long issueId, int limit) {
        Issue targetIssue = findIssue(issueId);
        return recommendAssignees(targetIssue, limit);
    }

    public List<AssigneeRecommendation> recommendAssignees(Issue targetIssue, int limit) {
        if (targetIssue == null) {
            throw new IllegalArgumentException("targetIssue must not be null");
        }

        int safeLimit = limit <= 0 ? DEFAULT_LIMIT : limit;
        List<Issue> allIssues = issueRepository.findAll();
        TermProfile targetProfile = TermProfile.fromIssue(targetIssue);

        Map<Long, CandidateScore> candidateScores = new LinkedHashMap<>();

        for (Issue historicalIssue : allIssues) {
            if (!canUseAsRecommendationHistory(targetIssue, historicalIssue)) {
                continue;
            }

            User fixer = historicalIssue.getFixer();
            TermProfile historicalProfile = TermProfile.fromIssue(historicalIssue);

            SimilarityResult similarity = calculateWeightedSimilarity(targetProfile, historicalProfile);

            if (similarity.score() <= 0.0) {
                continue;
            }

            double evidenceScore = calculateEvidenceScore(
                    similarity.score(),
                    targetIssue.getPriority(),
                    historicalIssue.getPriority(),
                    historicalIssue.getStatus(),
                    historicalIssue.getReportedDate()
            );

            CandidateScore candidateScore = candidateScores.computeIfAbsent(
                    fixer.getId(),
                    ignored -> new CandidateScore(fixer)
            );

            candidateScore.addEvidence(
                    evidenceScore,
                    similarity.matchedTerms(),
                    historicalIssue.getTitle()
            );
        }

        applyWorkloadPenalty(candidateScores, allIssues);

        return candidateScores.values().stream()
                .filter(candidate -> candidate.getFinalScore() > 0.0)
                .sorted(Comparator
                        .comparingDouble(CandidateScore::getFinalScore).reversed()
                        .thenComparing(Comparator.comparingInt(CandidateScore::getMatchedIssueCount).reversed())
                        .thenComparing(candidate -> candidate.getAssignee().getUsername()))
                .limit(safeLimit)
                .map(CandidateScore::toRecommendation)
                .toList();
    }

    private boolean canUseAsRecommendationHistory(Issue targetIssue, Issue historicalIssue) {
        if (historicalIssue == null) {
            return false;
        }

        if (targetIssue.getId() != null && targetIssue.getId().equals(historicalIssue.getId())) {
            return false;
        }

        if (!isResolvedHistory(historicalIssue.getStatus())) {
            return false;
        }

        User fixer = historicalIssue.getFixer();

        return fixer != null && fixer.hasRole(Role.DEV);
    }

    private boolean isResolvedHistory(IssueStatus status) {
        return status == IssueStatus.FIXED
                || status == IssueStatus.RESOLVED
                || status == IssueStatus.CLOSED;
    }

    private SimilarityResult calculateWeightedSimilarity(TermProfile targetProfile, TermProfile historicalProfile) {
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(targetProfile.termWeights().keySet());
        allTerms.addAll(historicalProfile.termWeights().keySet());

        double intersection = 0.0;
        double union = 0.0;
        Set<String> matchedTerms = new TreeSet<>();

        for (String term : allTerms) {
            double targetWeight = targetProfile.termWeights().getOrDefault(term, 0.0);
            double historicalWeight = historicalProfile.termWeights().getOrDefault(term, 0.0);

            intersection += Math.min(targetWeight, historicalWeight);
            union += Math.max(targetWeight, historicalWeight);

            if (targetWeight > 0.0 && historicalWeight > 0.0) {
                matchedTerms.add(term);
            }
        }

        if (union == 0.0) {
            return new SimilarityResult(0.0, matchedTerms);
        }

        return new SimilarityResult(intersection / union, matchedTerms);
    }

    private double calculateEvidenceScore(
            double similarity,
            Priority targetPriority,
            Priority historicalPriority,
            IssueStatus historicalStatus,
            LocalDateTime historicalReportedDate
    ) {
        double baseScore = similarity * 100.0;
        double priorityBonus = calculatePriorityBonus(targetPriority, historicalPriority);
        double statusMultiplier = calculateStatusMultiplier(historicalStatus);
        double recencyMultiplier = calculateRecencyMultiplier(historicalReportedDate);

        return (baseScore + priorityBonus) * statusMultiplier * recencyMultiplier;
    }

    private double calculatePriorityBonus(Priority targetPriority, Priority historicalPriority) {
        if (targetPriority == null || historicalPriority == null) {
            return 0.0;
        }

        if (targetPriority == historicalPriority) {
            return SAME_PRIORITY_BONUS;
        }

        int distance = Math.abs(targetPriority.ordinal() - historicalPriority.ordinal());

        if (distance == 1) {
            return SIMILAR_PRIORITY_BONUS;
        }

        return 0.0;
    }

    private double calculateStatusMultiplier(IssueStatus status) {
        if (status == IssueStatus.CLOSED) {
            return 1.15;
        }

        if (status == IssueStatus.RESOLVED) {
            return 1.10;
        }

        if (status == IssueStatus.FIXED) {
            return 1.00;
        }

        return 0.0;
    }

    private double calculateRecencyMultiplier(LocalDateTime historicalReportedDate) {
        if (historicalReportedDate == null) {
            return 1.0;
        }

        long days = Math.max(0, ChronoUnit.DAYS.between(historicalReportedDate, LocalDateTime.now()));

        if (days <= 30) {
            return 1.05;
        }

        if (days <= 180) {
            return 1.00;
        }

        if (days <= 365) {
            return 0.92;
        }

        return 0.85;
    }

    private void applyWorkloadPenalty(Map<Long, CandidateScore> candidateScores, List<Issue> allIssues) {
        for (CandidateScore candidateScore : candidateScores.values()) {
            int openAssignedIssueCount = countOpenAssignedIssues(candidateScore.getAssignee(), allIssues);
            double penalty = Math.min(MAX_WORKLOAD_PENALTY, openAssignedIssueCount * WORKLOAD_PENALTY_PER_OPEN_ISSUE);
            candidateScore.applyWorkloadPenalty(openAssignedIssueCount, penalty);
        }
    }

    private int countOpenAssignedIssues(User user, List<Issue> allIssues) {
        int count = 0;

        for (Issue issue : allIssues) {
            if (issue.getAssignee() == null) {
                continue;
            }

            if (!issue.getAssignee().getId().equals(user.getId())) {
                continue;
            }

            if (issue.getStatus() == IssueStatus.ASSIGNED || issue.getStatus() == IssueStatus.REOPENED) {
                count++;
            }
        }

        return count;
    }

    private Issue findIssue(Long issueId) {
        Optional<Issue> issue = issueRepository.findById(issueId);
        return issue.orElseThrow(() -> new IllegalArgumentException("issue not found: " + issueId));
    }

    private static Set<String> tokenize(String text) {
        Set<String> terms = new TreeSet<>();

        if (text == null || text.isBlank()) {
            return terms;
        }

        String[] tokens = text.toLowerCase(Locale.ROOT).split("[^a-z0-9가-힣]+");

        for (String token : tokens) {
            if (token.length() < 2) {
                continue;
            }

            if (STOP_WORDS.contains(token)) {
                continue;
            }

            terms.add(token);
        }

        return terms;
    }

    private record SimilarityResult(double score, Set<String> matchedTerms) {
    }

    private record TermProfile(Map<String, Double> termWeights) {
        private static TermProfile fromIssue(Issue issue) {
            Map<String, Double> weights = new HashMap<>();

            addWeightedTerms(weights, tokenize(issue.getTitle()), TITLE_TERM_WEIGHT);
            addWeightedTerms(weights, tokenize(issue.getDescription()), DESCRIPTION_TERM_WEIGHT);

            return new TermProfile(weights);
        }

        private static void addWeightedTerms(Map<String, Double> weights, Set<String> terms, double weight) {
            for (String term : terms) {
                weights.put(term, weights.getOrDefault(term, 0.0) + weight);
            }
        }
    }

    private static class CandidateScore {
        private final User assignee;
        private double evidenceScore;
        private double workloadPenalty;
        private int currentOpenAssignedIssueCount;
        private int matchedIssueCount;
        private final Set<String> matchedTerms = new TreeSet<>();
        private final List<String> evidenceIssueTitles = new ArrayList<>();

        private CandidateScore(User assignee) {
            this.assignee = assignee;
        }

        private User getAssignee() {
            return assignee;
        }

        private int getMatchedIssueCount() {
            return matchedIssueCount;
        }

        private double getFinalScore() {
            return Math.max(0.0, evidenceScore - workloadPenalty);
        }

        private void addEvidence(double score, Set<String> terms, String issueTitle) {
            this.evidenceScore += score;
            this.matchedIssueCount++;
            this.matchedTerms.addAll(terms);

            if (issueTitle != null && !issueTitle.isBlank()) {
                this.evidenceIssueTitles.add(issueTitle);
            }
        }

        private void applyWorkloadPenalty(int currentOpenAssignedIssueCount, double workloadPenalty) {
            this.currentOpenAssignedIssueCount = currentOpenAssignedIssueCount;
            this.workloadPenalty = workloadPenalty;
        }

        private AssigneeRecommendation toRecommendation() {
            return new AssigneeRecommendation(
                    assignee,
                    Math.round(getFinalScore() * 10.0) / 10.0,
                    matchedIssueCount,
                    matchedTerms,
                    evidenceIssueTitles,
                    currentOpenAssignedIssueCount
            );
        }
    }
}