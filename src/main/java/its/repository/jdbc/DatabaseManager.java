package its.repository.jdbc;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final String jdbcUrl;

    public DatabaseManager(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalArgumentException("jdbcUrl must not be blank");
        }
        this.jdbcUrl = jdbcUrl;
    }

    public static DatabaseManager fileDatabase(Path databasePath) {
        if (databasePath == null) {
            throw new IllegalArgumentException("databasePath must not be null");
        }
        return new DatabaseManager("jdbc:sqlite:" + databasePath.toAbsolutePath());
    }

    public static DatabaseManager memoryDatabase() {
        return new DatabaseManager("jdbc:sqlite::memory:");
    }

    public Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);
            enableForeignKeys(connection);
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to open database connection", e);
        }
    }

    private void enableForeignKeys(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }
}