package its.repository.jdbc;

import its.domain.issue.Comment;
import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.project.Project;
import its.domain.user.Role;
import its.domain.user.User;
import its.repository.issue.IssueRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcIssueRepository implements IssueRepository {
    private final DatabaseManager databaseManager;

    public JdbcIssueRepository(DatabaseManager databaseManager) {
        if (databaseManager == null) {
            throw new IllegalArgumentException("databaseManager must not be null");
        }
        this.databaseManager = databaseManager;
    }

    @Override
    public Issue save(Issue issue) {
        if (issue.getId() == null) {
            return insert(issue);
        }

        update(issue);
        return issue;
    }

    private Issue insert(Issue issue) {
        String sql = """
            INSERT INTO issues (
                project_id,
                title,
                description,
                reporter_id,
                reported_date,
                fixer_id,
                assignee_id,
                priority,
                status
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            setIssueStatementValues(statement, issue);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    issue.setId(keys.getLong(1));
                }
            }

            return issue;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to save issue", e);
        }
    }

    @Override
    public void update(Issue issue) {
        String sql = """
            UPDATE issues
            SET project_id = ?,
                title = ?,
                description = ?,
                reporter_id = ?,
                reported_date = ?,
                fixer_id = ?,
                assignee_id = ?,
                priority = ?,
                status = ?
            WHERE id = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setIssueStatementValues(statement, issue);
            statement.setLong(10, issue.getId());

            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new IllegalArgumentException("issue not found: " + issue.getId());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to update issue", e);
        }
    }

    @Override
    public Optional<Issue> findById(Long id) {
        String sql = """
            SELECT id,
                   project_id,
                   title,
                   description,
                   reporter_id,
                   reported_date,
                   fixer_id,
                   assignee_id,
                   priority,
                   status
            FROM issues
            WHERE id = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Issue issue = mapIssue(resultSet);
                    issue.setComments(findCommentsByIssue(issue.getId()));
                    return Optional.of(issue);
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find issue by id", e);
        }
    }

    @Override
    public List<Issue> findAll() {
        String sql = """
            SELECT id,
                   project_id,
                   title,
                   description,
                   reporter_id,
                   reported_date,
                   fixer_id,
                   assignee_id,
                   priority,
                   status
            FROM issues
            ORDER BY id
            """;

        List<Issue> issues = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Issue issue = mapIssue(resultSet);
                issue.setComments(findCommentsByIssue(issue.getId()));
                issues.add(issue);
            }

            return issues;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find all issues", e);
        }
    }

    @Override
    public List<Issue> findByStatus(IssueStatus status) {
        String sql = """
            SELECT id,
                   project_id,
                   title,
                   description,
                   reporter_id,
                   reported_date,
                   fixer_id,
                   assignee_id,
                   priority,
                   status
            FROM issues
            WHERE status = ?
            ORDER BY id
            """;

        List<Issue> issues = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Issue issue = mapIssue(resultSet);
                    issue.setComments(findCommentsByIssue(issue.getId()));
                    issues.add(issue);
                }
            }

            return issues;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find issues by status", e);
        }
    }

    @Override
    public void addComment(Comment comment) {
        String sql = """
            INSERT INTO comments (issue_id, author_id, content, created_at)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, comment.getIssueId());
            statement.setLong(2, comment.getAuthor().getId());
            statement.setString(3, comment.getContent());
            statement.setString(4, comment.getCreatedAt().toString());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    comment.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to add comment", e);
        }
    }

    @Override
    public List<Comment> findCommentsByIssue(Long issueId) {
        String sql = """
            SELECT id, issue_id, author_id, content, created_at
            FROM comments
            WHERE issue_id = ?
            ORDER BY id
            """;

        List<Comment> comments = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, issueId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comments.add(mapComment(resultSet));
                }
            }

            return comments;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find comments by issue", e);
        }
    }

    private void setIssueStatementValues(PreparedStatement statement, Issue issue) throws SQLException {
        statement.setLong(1, issue.getProject().getId());
        statement.setString(2, issue.getTitle());
        statement.setString(3, issue.getDescription());
        statement.setLong(4, issue.getReporter().getId());
        statement.setString(5, issue.getReportedDate().toString());

        if (issue.getFixer() == null) {
            statement.setObject(6, null);
        } else {
            statement.setLong(6, issue.getFixer().getId());
        }

        if (issue.getAssignee() == null) {
            statement.setObject(7, null);
        } else {
            statement.setLong(7, issue.getAssignee().getId());
        }

        statement.setString(8, issue.getPriority().name());
        statement.setString(9, issue.getStatus().name());
    }

    private Issue mapIssue(ResultSet resultSet) throws SQLException {
        Project project = findProject(resultSet.getLong("project_id"));
        User reporter = findUser(resultSet.getLong("reporter_id"));

        Long fixerId = getNullableLong(resultSet, "fixer_id");
        User fixer = fixerId == null ? null : findUser(fixerId);

        Long assigneeId = getNullableLong(resultSet, "assignee_id");
        User assignee = assigneeId == null ? null : findUser(assigneeId);

        return new Issue(
                resultSet.getLong("id"),
                project,
                resultSet.getString("title"),
                resultSet.getString("description"),
                reporter,
                LocalDateTime.parse(resultSet.getString("reported_date")),
                fixer,
                assignee,
                Priority.valueOf(resultSet.getString("priority")),
                IssueStatus.valueOf(resultSet.getString("status"))
        );
    }

    private Comment mapComment(ResultSet resultSet) throws SQLException {
        User author = findUser(resultSet.getLong("author_id"));

        return new Comment(
                resultSet.getLong("id"),
                resultSet.getLong("issue_id"),
                author,
                resultSet.getString("content"),
                LocalDateTime.parse(resultSet.getString("created_at"))
        );
    }

    private User findUser(Long id) {
        String sql = """
            SELECT id, username, password, role
            FROM users
            WHERE id = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            Role.valueOf(resultSet.getString("role"))
                    );
                }

                throw new IllegalArgumentException("user not found: " + id);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find user while mapping issue", e);
        }
    }

    private Project findProject(Long id) {
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
                    return new Project(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            LocalDateTime.parse(resultSet.getString("created_at"))
                    );
                }

                throw new IllegalArgumentException("project not found: " + id);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find project while mapping issue", e);
        }
    }

    private Long getNullableLong(ResultSet resultSet, String columnName) throws SQLException {
        long value = resultSet.getLong(columnName);
        return resultSet.wasNull() ? null : value;
    }
}