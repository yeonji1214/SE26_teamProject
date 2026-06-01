package its.api.dto;

public class CommentResponse {
    private Long id;
    private Long issueId;
    private UserResponse author;
    private String content;
    private String createdAt;

    public CommentResponse() {
    }

    public CommentResponse(Long id, Long issueId, UserResponse author, String content, String createdAt) {
        this.id = id;
        this.issueId = issueId;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public UserResponse getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setAuthor(UserResponse author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}