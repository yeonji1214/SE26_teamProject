package its.api.dto;

import java.util.ArrayList;
import java.util.List;

public class IssueResponse {
    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private UserResponse reporter;
    private UserResponse assignee;
    private UserResponse fixer;
    private String priority;
    private String status;
    private String reportedDate;
    private List<CommentResponse> comments = new ArrayList<>();

    public IssueResponse() {
    }

    public IssueResponse(
            Long id,
            Long projectId,
            String title,
            String description,
            UserResponse reporter,
            UserResponse assignee,
            UserResponse fixer,
            String priority,
            String status,
            String reportedDate,
            List<CommentResponse> comments
    ) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.reporter = reporter;
        this.assignee = assignee;
        this.fixer = fixer;
        this.priority = priority;
        this.status = status;
        this.reportedDate = reportedDate;
        this.comments = comments == null ? new ArrayList<>() : new ArrayList<>(comments);
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public UserResponse getReporter() {
        return reporter;
    }

    public UserResponse getAssignee() {
        return assignee;
    }

    public UserResponse getFixer() {
        return fixer;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getReportedDate() {
        return reportedDate;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReporter(UserResponse reporter) {
        this.reporter = reporter;
    }

    public void setAssignee(UserResponse assignee) {
        this.assignee = assignee;
    }

    public void setFixer(UserResponse fixer) {
        this.fixer = fixer;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
    }

    public void setComments(List<CommentResponse> comments) {
        this.comments = comments == null ? new ArrayList<>() : new ArrayList<>(comments);
    }
}