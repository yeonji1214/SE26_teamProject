package its.service;

import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.project.Project;
import its.domain.user.Role;
import its.domain.user.User;
import its.repository.issue.InMemoryIssueRepository;
import its.repository.issue.IssueRepository;
import its.repository.project.InMemoryProjectRepository;
import its.repository.project.ProjectRepository;
import its.repository.user.InMemoryUserRepository;
import its.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssueServiceTest {
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private IssueRepository issueRepository;
    private UserService userService;
    private ProjectService projectService;
    private IssueService issueService;

    private User pl1;
    private User dev1;
    private User tester1;
    private Project project1;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        projectRepository = new InMemoryProjectRepository();
        issueRepository = new InMemoryIssueRepository();

        userService = new UserService(userRepository);
        projectService = new ProjectService(projectRepository);
        issueService = new IssueService(issueRepository, userRepository, projectRepository);

        pl1 = userService.createUser("PL1", "pw", Role.PL);
        dev1 = userService.createUser("dev1", "pw", Role.DEV);
        tester1 = userService.createUser("tester1", "pw", Role.TESTER);
        project1 = projectService.createProject("project1", "demo project");
    }

    @Test
    void createIssueStoresReporterReportedDateDefaultStatusAndDefaultPriority() {
        Issue issue = issueService.createIssue(
                project1.getId(),
                "Login error",
                "Login fails after redirect",
                tester1.getId(),
                null
        );

        assertNotNull(issue.getId());
        assertEquals("Login error", issue.getTitle());
        assertEquals(tester1.getId(), issue.getReporter().getId());
        assertNotNull(issue.getReportedDate());
        assertEquals(IssueStatus.NEW, issue.getStatus());
        assertEquals(Priority.MAJOR, issue.getPriority());
    }

    @Test
    void searchIssuesCanFilterByStatusReporterAssigneeAndKeyword() {
        Issue issue = issueService.createIssue(
                project1.getId(),
                "Search button bug",
                "Button does not react",
                tester1.getId(),
                Priority.CRITICAL
        );
        issueService.assignIssue(issue.getId(), pl1.getId(), dev1.getId(), "assign to dev1");

        IssueSearchCriteria criteria = new IssueSearchCriteria()
                .setStatus(IssueStatus.ASSIGNED)
                .setReporterId(tester1.getId())
                .setAssigneeId(dev1.getId())
                .setKeyword("button");

        List<Issue> result = issueService.searchIssues(criteria);

        assertEquals(1, result.size());
        assertEquals(issue.getId(), result.get(0).getId());
    }

    @Test
    void issueLifecycleFollowsScenarioNewAssignedFixedResolvedClosed() {
        Issue issue = issueService.createIssue(
                project1.getId(),
                "Crash on save",
                "App crashes when saving issue",
                tester1.getId(),
                Priority.BLOCKER
        );

        issueService.addComment(issue.getId(), tester1.getId(), "please check this issue");
        issueService.assignIssue(issue.getId(), pl1.getId(), dev1.getId(), "please fix this");
        issueService.markFixed(issue.getId(), dev1.getId(), "fixed in local code");
        issueService.resolveIssue(issue.getId(), tester1.getId(), "verified");
        Issue closed = issueService.closeIssue(issue.getId(), pl1.getId(), "close issue");

        assertEquals(IssueStatus.CLOSED, closed.getStatus());
        assertEquals(dev1.getId(), closed.getFixer().getId());
        assertEquals(5, issueService.getComments(issue.getId()).size());
    }

    @Test
    void nonDevCannotBeAssignedAsAssignee() {
        Issue issue = issueService.createIssue(
                project1.getId(),
                "Permission bug",
                "Permission page has wrong value",
                tester1.getId(),
                Priority.MAJOR
        );

        assertThrows(IllegalArgumentException.class,
                () -> issueService.assignIssue(issue.getId(), pl1.getId(), tester1.getId(), "wrong assignee"));
    }
}
