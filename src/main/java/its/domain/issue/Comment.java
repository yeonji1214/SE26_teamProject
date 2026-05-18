package its.domain.issue;

import its.domain.user.User;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Long issueId;
    private User author;
    private String content;
    private LocalDateTime createdAt;

    public Comment(Long id, Long issueId, User author, String content, LocalDateTime createdAt) {
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

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
