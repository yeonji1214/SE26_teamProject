package its.service;

import its.domain.user.Role;
import its.domain.user.User;
import its.repository.user.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("duplicated username: " + username);
        }
        return userRepository.save(new User(null, username, password, role));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
