package its.repository.jdbc;

import its.domain.project.Project;
import its.repository.project.ProjectRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProjectRepository implements ProjectRepository {
    private final DatabaseManager databaseManager;

    public JdbcProjectRepository(DatabaseManager databaseManager) {
        if (databaseManager == null) {
            throw new IllegalArgumentException("databaseManager must not be null");
        }
        this.databaseManager = databaseManager;
    }

    @Override
    public Project save(Project project) {
        if (project.getId() == null) {
            return insert(project);
        }
        update(project);
        return project;
    }

    private Project insert(Project project) {
        String sql = """
            INSERT INTO projects (name, description, created_at)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setString(3, project.getCreatedAt().toString());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    project.setId(keys.getLong(1));
                }
            }

            return project;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to save project", e);
        }
    }

    private void update(Project project) {
        String sql = """
            UPDATE projects
            SET name = ?, description = ?
            WHERE id = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setLong(3, project.getId());

            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new IllegalArgumentException("project not found: " + project.getId());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to update project", e);
        }
    }

    @Override
    public Optional<Project> findById(Long id) {
        String sql = """
            SELECT id, name, description, created_at
            FROM projects
            WHERE id = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapProject(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find project by id", e);
        }
    }

    @Override
    public Optional<Project> findByName(String name) {
        String sql = """
            SELECT id, name, description, created_at
            FROM projects
            WHERE name = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapProject(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find project by name", e);
        }
    }

    @Override
    public List<Project> findAll() {
        String sql = """
            SELECT id, name, description, created_at
            FROM projects
            ORDER BY id
            """;

        List<Project> projects = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                projects.add(mapProject(resultSet));
            }

            return projects;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find all projects", e);
        }
    }

    private Project mapProject(ResultSet resultSet) throws SQLException {
        return new Project(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                LocalDateTime.parse(resultSet.getString("created_at"))
        );
    }
}