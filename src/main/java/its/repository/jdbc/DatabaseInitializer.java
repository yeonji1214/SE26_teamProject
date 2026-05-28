package its.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final DatabaseManager databaseManager;

    public DatabaseInitializer(DatabaseManager databaseManager) {
        if (databaseManager == null) {
            throw new IllegalArgumentException("databaseManager must not be null");
        }
        this.databaseManager = databaseManager;
    }

    public void initialize() {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS projects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    description TEXT
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS issues (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    project_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    reporter_id INTEGER NOT NULL,
                    reported_date TEXT NOT NULL,
                    fixer_id INTEGER,
                    assignee_id INTEGER,
                    priority TEXT NOT NULL,
                    status TEXT NOT NULL,
                    FOREIGN KEY (project_id) REFERENCES projects(id),
                    FOREIGN KEY (reporter_id) REFERENCES users(id),
                    FOREIGN KEY (fixer_id) REFERENCES users(id),
                    FOREIGN KEY (assignee_id) REFERENCES users(id)
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS comments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    issue_id INTEGER NOT NULL,
                    author_id INTEGER NOT NULL,
                    content TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    FOREIGN KEY (issue_id) REFERENCES issues(id) ON DELETE CASCADE,
                    FOREIGN KEY (author_id) REFERENCES users(id)
                )
                """);

            statement.execute("CREATE INDEX IF NOT EXISTS idx_issues_project_id ON issues(project_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_issues_status ON issues(status)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_issues_assignee_id ON issues(assignee_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_issues_reporter_id ON issues(reporter_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_comments_issue_id ON comments(issue_id)");

        } catch (SQLException e) {
            throw new IllegalStateException("failed to initialize database schema", e);
        }
    }
}