package its.api.dto;

public class UpdateIssueStatusRequest {
    private String status;
    private Long actorId;
    private Long assigneeId;
    private Long fixerId;
    private String comment;

    public UpdateIssueStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public Long getActorId() {
        return actorId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public Long getFixerId() {
        return fixerId;
    }

    public String getComment() {
        return comment;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public void setFixerId(Long fixerId) {
        this.fixerId = fixerId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}