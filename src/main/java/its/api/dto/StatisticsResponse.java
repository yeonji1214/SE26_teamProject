package its.api.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsResponse {
    private long totalIssues;
    private Map<String, Long> byStatus = new LinkedHashMap<>();
    private Map<String, Long> byPriority = new LinkedHashMap<>();
    private Map<String, Long> byDay = new LinkedHashMap<>();
    private Map<String, Long> byMonth = new LinkedHashMap<>();
    private Map<String, Long> byAssignee = new LinkedHashMap<>();

    public StatisticsResponse() {
    }

    public StatisticsResponse(
            long totalIssues,
            Map<String, Long> byStatus,
            Map<String, Long> byPriority,
            Map<String, Long> byDay,
            Map<String, Long> byMonth,
            Map<String, Long> byAssignee
    ) {
        this.totalIssues = totalIssues;
        this.byStatus = copyMap(byStatus);
        this.byPriority = copyMap(byPriority);
        this.byDay = copyMap(byDay);
        this.byMonth = copyMap(byMonth);
        this.byAssignee = copyMap(byAssignee);
    }

    public long getTotalIssues() {
        return totalIssues;
    }

    public Map<String, Long> getByStatus() {
        return byStatus;
    }

    public Map<String, Long> getByPriority() {
        return byPriority;
    }

    public Map<String, Long> getByDay() {
        return byDay;
    }

    public Map<String, Long> getByMonth() {
        return byMonth;
    }

    public Map<String, Long> getByAssignee() {
        return byAssignee;
    }

    public void setTotalIssues(long totalIssues) {
        this.totalIssues = totalIssues;
    }

    public void setByStatus(Map<String, Long> byStatus) {
        this.byStatus = copyMap(byStatus);
    }

    public void setByPriority(Map<String, Long> byPriority) {
        this.byPriority = copyMap(byPriority);
    }

    public void setByDay(Map<String, Long> byDay) {
        this.byDay = copyMap(byDay);
    }

    public void setByMonth(Map<String, Long> byMonth) {
        this.byMonth = copyMap(byMonth);
    }

    public void setByAssignee(Map<String, Long> byAssignee) {
        this.byAssignee = copyMap(byAssignee);
    }

    private static Map<String, Long> copyMap(Map<String, Long> source) {
        return source == null ? new LinkedHashMap<>() : new LinkedHashMap<>(source);
    }
}