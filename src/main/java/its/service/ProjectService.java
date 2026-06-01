package its.service;

import its.domain.project.Project;
import its.repository.project.ProjectRepository;

import java.util.List;

public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String name, String description) {
        if (projectRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("duplicated project name: " + name);
        }
        return projectRepository.save(new Project(null, name, description));
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("project not found: " + projectId));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}
