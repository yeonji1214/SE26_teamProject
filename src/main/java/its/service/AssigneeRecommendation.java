package its.service;

import its.domain.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AssigneeRecommendation {
    private final User assignee;
    private final double score;
    private final int matchedIssueCount;
    private final Set<String> matchedTerms;
    private final List<String> evidenceIssueTitles;
    private final int currentOpenAssignedIssueCount;

    public AssigneeRecommendation(
            User assignee,
            double score,
            int matchedIssueCount,
            Set<String> matchedTerms,
            List<String> evidenceIssueTitles,
            int currentOpenAssignedIssueCount
    ) {
        if (assignee == null) {
            throw new IllegalArgumentException("assignee must not be null");
        }

        this.assignee = assignee;
        this.score = score;
        this.matchedIssueCount = matchedIssueCount;
        this.matchedTerms = Collections.unmodifiableSet(new TreeSet<>(matchedTerms));
        this.evidenceIssueTitles = Collections.unmodifiableList(new ArrayList<>(evidenceIssueTitles));
        this.currentOpenAssignedIssueCount = currentOpenAssignedIssueCount;
    }

    public User getAssignee() {
        return assignee;
    }

    public double getScore() {
        return score;
    }

    public int getMatchedIssueCount() {
        return matchedIssueCount;
    }

    public Set<String> getMatchedTerms() {
        return matchedTerms;
    }

    public List<String> getEvidenceIssueTitles() {
        return evidenceIssueTitles;
    }

    public int getCurrentOpenAssignedIssueCount() {
        return currentOpenAssignedIssueCount;
    }

    public String getExplanation() {
        return assignee.getUsername()
                + " is recommended because "
                + matchedIssueCount
                + " similar resolved issue(s) were found. matchedTerms="
                + matchedTerms
                + ", evidence="
                + evidenceIssueTitles
                + ", currentOpenAssignedIssues="
                + currentOpenAssignedIssueCount;
    }
}