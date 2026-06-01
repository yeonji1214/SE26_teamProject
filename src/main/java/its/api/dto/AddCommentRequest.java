package its.api.dto;

public class AddCommentRequest {
    private Long authorId;
    private String content;

    public AddCommentRequest() {
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}