package its.repository.user;

import its.domain.user.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new LinkedHashMap<>();
    private long sequence = 0L;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++sequence);
        } else if (user.getId() > sequence) {
            sequence = user.getId();
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
