package its.service;

import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.repository.issue.IssueRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class StatisticsService {
    private final IssueRepository issueRepository;

    public StatisticsService(IssueRepository issueRepository) {
        this.issueRepository = Objects.requireNonNull(issueRepository, "issueRepository must not be null");
    }

    public Map<IssueStatus, Long> countIssuesByStatus() {
        Map<IssueStatus, Long> result = new EnumMap<>(IssueStatus.class);

        for (IssueStatus status : IssueStatus.values()) {
            result.put(status, 0L);
        }

        for (Issue issue : getAllIssues()) {
            result.put(issue.getStatus(), result.get(issue.getStatus()) + 1);
        }

        return result;
    }

    public Map<Priority, Long> countIssuesByPriority() {
        Map<Priority, Long> result = new EnumMap<>(Priority.class);

        for (Priority priority : Priority.values()) {
            result.put(priority, 0L);
        }

        for (Issue issue : getAllIssues()) {
            result.put(issue.getPriority(), result.get(issue.getPriority()) + 1);
        }

        return result;
    }

    public Map<LocalDate, Long> countIssuesByDay() {
        return getAllIssues().stream()
                .collect(Collectors.groupingBy(
                        issue -> issue.getReportedDate().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    public Map<YearMonth, Long> countIssuesByMonth() {
        return getAllIssues().stream()
                .collect(Collectors.groupingBy(
                        issue -> YearMonth.from(issue.getReportedDate()),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    public Map<String, Long> countIssuesByAssignee() {
        Map<String, Long> result = new LinkedHashMap<>();

        for (Issue issue : getAllIssues()) {
            String key = issue.getAssignee() == null
                    ? "UNASSIGNED"
                    : issue.getAssignee().getUsername();

            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }

    public long countTotalIssues() {
        return getAllIssues().size();
    }


    private List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }
}