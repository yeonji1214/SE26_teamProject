package its.api.dto;

public class CreateIssueRequest {
    private Long projectId;
    private String title;
    private String description;
    private String priority;
    private Long reporterId;

    public CreateIssueRequest() {
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

    public String getPriority() {
        return priority;
    }

    public Long getReporterId() {
        return reporterId;
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

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }
}