package its.domain.project;

public class Project {
    private Long id;
    private String name;
    private String description;

    public Project(Long id, String name, String description) {
        validateName(name);
        this.id = id;
        this.name = name;
        this.description = description == null ? "" : description;
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
