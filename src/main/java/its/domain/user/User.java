package its.domain.user;

import java.util.Objects;

public class User {
    private Long id;
    private String username;
    private String password;
    private Role role;

    public User(Long id, String username, String password, Role role) {
        validateUsername(username);
        validatePassword(password);
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        validateUsername(username);
        this.username = username;
    }

    public void setPassword(String password) {
        validatePassword(password);
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public boolean hasRole(Role role) {
        return this.role == role;
    }

    private static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }
    }
}
