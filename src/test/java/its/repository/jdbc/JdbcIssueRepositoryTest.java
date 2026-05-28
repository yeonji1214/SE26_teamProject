package its.repository.jdbc;

import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.project.Project;
import its.domain.user.Role;
import its.domain.user.User;
import its.repository.issue.IssueRepository;
import its.repository.project.ProjectRepository;
import its.repository.user.UserRepository;
import its.service.IssueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JdbcIssueRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void issueLifecycleIsPersistedInSqliteDatabase() {
        Path databasePath = tempDir.resolve("issue-lifecycle-test.db");

        DatabaseManager databaseManager = DatabaseManager.fileDatabase(databasePath);
        new DatabaseInitializer(databaseManager).initialize();

        UserRepository userRepository = new JdbcUserRepository(databaseManager);
        ProjectRepository projectRepository = new JdbcProjectRepository(databaseManager);
        IssueRepository issueRepository = new JdbcIssueRepository(databaseManager);

        User tester1 = userRepository.save(new User(null, "tester1", "pw", Role.TESTER));
        User pl1 = userRepository.save(new User(null, "PL1", "pw", Role.PL));
        User dev1 = userRepository.save(new User(null, "dev1", "pw", Role.DEV));
        Project project1 = projectRepository.save(new Project(null, "project1", "demo project"));

        IssueService issueService = new IssueService(issueRepository, userRepository, projectRepository);

        Issue issue = issueService.createIssue(
                project1.getId(),
                "Crash on save",
                "App crashes when saving issue",
                tester1.getId(),
                Priority.BLOCKER
        );

        issueService.addComment(issue.getId(), tester1.getId(), "please check this issue");
        issueService.assignIssue(issue.getId(), pl1.getId(), dev1.getId(), "please fix this");
        issueService.markFixed(issue.getId(), dev1.getId(), "fixed save logic");
        issueService.resolveIssue(issue.getId(), tester1.getId(), "verified");
        Issue closed = issueService.closeIssue(issue.getId(), pl1.getId(), "close issue");

        Issue loaded = issueService.getIssue(issue.getId());

        assertNotNull(loaded.getId());
        assertEquals(IssueStatus.CLOSED, closed.getStatus());
        assertEquals(IssueStatus.CLOSED, loaded.getStatus());
        assertEquals(dev1.getId(), loaded.getFixer().getId());
        assertEquals(dev1.getId(), loaded.getAssignee().getId());
        assertEquals(5, issueService.getComments(issue.getId()).size());
    }
}