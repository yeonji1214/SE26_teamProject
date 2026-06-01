package its.api.dto;

public class LoginRequest {
    private Long userId;

    public LoginRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}