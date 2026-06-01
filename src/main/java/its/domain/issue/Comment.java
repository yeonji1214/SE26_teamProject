package its.domain.issue;

import its.domain.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {
    private Long id;
    private Long issueId;
    private User author;
    private String content;
    private LocalDateTime createdAt;

    public Comment(Long id, Long issueId, User author, String content, LocalDateTime createdAt) {
        validateContent(content);
        this.id = id;
        this.issueId = issueId;
        this.author = Objects.requireNonNull(author, "author must not be null");
        this.content = content;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
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
        this.author = Objects.requireNonNull(author, "author must not be null");
    }

    public void setContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("comment content must not be blank");
        }
    }
}
