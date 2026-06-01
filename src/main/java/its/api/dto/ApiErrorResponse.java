package its.api.dto;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    private String error;
    private String message;
    private int status;
    private String path;
    private String timestamp;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String error, String message, int status, String path) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}