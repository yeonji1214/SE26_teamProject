package its.domain.project;

import java.time.LocalDateTime;
import java.util.Objects;

public class Project {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public Project(Long id, String name, String description) {
        this(id, name, description, LocalDateTime.now());
    }

    public Project(Long id, String name, String description, LocalDateTime createdAt) {
        validateName(name);
        this.id = id;
        this.name = name;
        this.description = description == null ? "" : description;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("project name must not be blank");
        }
    }
}