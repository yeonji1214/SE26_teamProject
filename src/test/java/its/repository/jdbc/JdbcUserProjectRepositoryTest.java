package its.repository.jdbc;

import its.domain.project.Project;
import its.domain.user.Role;
import its.domain.user.User;
import its.repository.project.ProjectRepository;
import its.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcUserProjectRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void userAndProjectAreSavedAndLoadedFromDatabase() {
        Path databasePath = tempDir.resolve("issue-tracker-test.db");

        DatabaseManager databaseManager = DatabaseManager.fileDatabase(databasePath);
        new DatabaseInitializer(databaseManager).initialize();

        UserRepository userRepository = new JdbcUserRepository(databaseManager);
        ProjectRepository projectRepository = new JdbcProjectRepository(databaseManager);

        User savedUser = userRepository.save(new User(null, "tester1", "pw", Role.TESTER));
        Project savedProject = projectRepository.save(new Project(null, "project1", "demo project"));

        User loadedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        Project loadedProject = projectRepository.findById(savedProject.getId()).orElseThrow();

        assertEquals("tester1", loadedUser.getUsername());
        assertEquals(Role.TESTER, loadedUser.getRole());
        assertEquals("project1", loadedProject.getName());
        assertEquals("demo project", loadedProject.getDescription());

        assertTrue(userRepository.findByUsername("tester1").isPresent());
        assertTrue(projectRepository.findByName("project1").isPresent());
    }
}