package its.service;

import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;

public class IssueSearchCriteria {
    private Long projectId;
    private Long reporterId;
    private Long assigneeId;
    private IssueStatus status;
    private Priority priority;
    private String keyword;

    public Long getProjectId() {
        return projectId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getKeyword() {
        return keyword;
    }

    public IssueSearchCriteria setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public IssueSearchCriteria setReporterId(Long reporterId) {
        this.reporterId = reporterId;
        return this;
    }

    public IssueSearchCriteria setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    public IssueSearchCriteria setStatus(IssueStatus status) {
        this.status = status;
        return this;
    }

    public IssueSearchCriteria setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public IssueSearchCriteria setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }
}
