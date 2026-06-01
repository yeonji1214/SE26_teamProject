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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsServiceTest {
    private IssueService issueService;
    private StatisticsService statisticsService;

    private User tester1;
    private User pl1;
    private User dev1;
    private Project project1;

    @BeforeEach
    void setUp() {
        IssueRepository issueRepository = new InMemoryIssueRepository();
        UserRepository userRepository = new InMemoryUserRepository();
        ProjectRepository projectRepository = new InMemoryProjectRepository();

        UserService userService = new UserService(userRepository);
        ProjectService projectService = new ProjectService(projectRepository);

        issueService = new IssueService(issueRepository, userRepository, projectRepository);
        statisticsService = new StatisticsService(issueRepository);

        tester1 = userService.createUser("tester1", "pw", Role.TESTER);
        pl1 = userService.createUser("PL1", "pw", Role.PL);
        dev1 = userService.createUser("dev1", "pw", Role.DEV);
        project1 = projectService.createProject("project1", "demo project");
    }

    @Test
    void countIssuesByStatusAndPriority() {
        issueService.createIssue(
                project1.getId(),
                "Login bug",
                "Login fails with valid account",
                tester1.getId(),
                Priority.MAJOR
        );

        Issue blockerIssue = issueService.createIssue(
                project1.getId(),
                "Crash on save",
                "App crashes when saving issue",
                tester1.getId(),
                Priority.BLOCKER
        );

        issueService.assignIssue(blockerIssue.getId(), pl1.getId(), dev1.getId(), "please fix this");

        Map<IssueStatus, Long> byStatus = statisticsService.countIssuesByStatus();
        Map<Priority, Long> byPriority = statisticsService.countIssuesByPriority();

        assertEquals(1L, byStatus.get(IssueStatus.NEW));
        assertEquals(1L, byStatus.get(IssueStatus.ASSIGNED));
        assertEquals(1L, byPriority.get(Priority.MAJOR));
        assertEquals(1L, byPriority.get(Priority.BLOCKER));
        assertEquals(2L, statisticsService.countTotalIssues());
    }

    @Test
    void countIssuesByDayMonthAndAssignee() {
        Issue issue = issueService.createIssue(
                project1.getId(),
                "Search error",
                "Search result is wrong",
                tester1.getId(),
                Priority.MINOR
        );

        issueService.assignIssue(issue.getId(), pl1.getId(), dev1.getId(), "check search module");

        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.now();

        assertEquals(1L, statisticsService.countIssuesByDay().get(today));
        assertEquals(1L, statisticsService.countIssuesByMonth().get(thisMonth));
        assertEquals(1L, statisticsService.countIssuesByAssignee().get(dev1.getUsername()));
    }
}