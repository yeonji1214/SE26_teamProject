package its.repository.project;

import its.domain.project.Project;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProjectRepository implements ProjectRepository {
    private final Map<Long, Project> projects = new LinkedHashMap<>();
    private long sequence = 0L;

    @Override
    public Project save(Project project) {
        if (project.getId() == null) {
            project.setId(++sequence);
        } else if (project.getId() > sequence) {
            sequence = project.getId();
        }
        projects.put(project.getId(), project);
        return project;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return Optional.ofNullable(projects.get(id));
    }

    @Override
    public Optional<Project> findByName(String name) {
        return projects.values().stream()
                .filter(project -> project.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(projects.values());
    }
}
