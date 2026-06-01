package its.repository.jdbc;

import its.domain.user.Role;
import its.domain.user.User;
import its.repository.user.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {
    private final DatabaseManager databaseManager;

    public JdbcUserRepository(DatabaseManager databaseManager) {
        if (databaseManager == null) {
            throw new IllegalArgumentException("databaseManager must not be null");
        }
        this.databaseManager = databaseManager;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return insert(user);
        }
        update(user);
        return user;
    }

    private User insert(User user) {
        String sql = """
            INSERT INTO users (username, password, role)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().name());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }

            return user;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to save user", e);
        }
    }

    private void update(User user) {
        String sql = """
            UPDATE users
            SET username = ?, password = ?, role = ?
            WHERE id = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().name());
            statement.setLong(4, user.getId());

            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new IllegalArgumentException("user not found: " + user.getId());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to update user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
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
                    return Optional.of(mapUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find user by id", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
            SELECT id, username, password, role
            FROM users
            WHERE username = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find user by username", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = """
            SELECT id, username, password, role
            FROM users
            ORDER BY id
            """;

        List<User> users = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

            return users;
        } catch (SQLException e) {
            throw new IllegalStateException("failed to find all users", e);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                Role.valueOf(resultSet.getString("role"))
        );
    }
}