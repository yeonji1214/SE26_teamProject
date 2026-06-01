package its.service;

import its.domain.issue.Issue;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationServiceTest {
    private IssueService issueService;
    private RecommendationService recommendationService;

    private User tester1;
    private User pl1;
    private User dev1;
    private User dev2;
    private Project project1;

    @BeforeEach
    void setUp() {
        IssueRepository issueRepository = new InMemoryIssueRepository();
        UserRepository userRepository = new InMemoryUserRepository();
        ProjectRepository projectRepository = new InMemoryProjectRepository();

        UserService userService = new UserService(userRepository);
        ProjectService projectService = new ProjectService(projectRepository);

        issueService = new IssueService(issueRepository, userRepository, projectRepository);
        recommendationService = new RecommendationService(issueRepository);

        tester1 = userService.createUser("tester1", "pw", Role.TESTER);
        pl1 = userService.createUser("PL1", "pw", Role.PL);
        dev1 = userService.createUser("dev1", "pw", Role.DEV);
        dev2 = userService.createUser("dev2", "pw", Role.DEV);
        project1 = projectService.createProject("project1", "demo project");
    }

    @Test
    void recommendDeveloperWithExplainableEvidenceFromSimilarResolvedIssues() {
        Issue loginHistory = issueService.createIssue(
                project1.getId(),
                "Login validation error",
                "Login fails when user submits valid account",
                tester1.getId(),
                Priority.MAJOR
        );

        issueService.assignIssue(loginHistory.getId(), pl1.getId(), dev1.getId(), "please fix login validation");
        issueService.markFixed(loginHistory.getId(), dev1.getId(), "fixed login validation");
        issueService.resolveIssue(loginHistory.getId(), tester1.getId(), "verified");
        issueService.closeIssue(loginHistory.getId(), pl1.getId(), "closed");

        Issue databaseHistory = issueService.createIssue(
                project1.getId(),
                "Database save failure",
                "Issue is not saved into database",
                tester1.getId(),
                Priority.CRITICAL
        );

        issueService.assignIssue(databaseHistory.getId(), pl1.getId(), dev2.getId(), "please fix database");
        issueService.markFixed(databaseHistory.getId(), dev2.getId(), "fixed database save");
        issueService.resolveIssue(databaseHistory.getId(), tester1.getId(), "verified");
        issueService.closeIssue(databaseHistory.getId(), pl1.getId(), "closed");

        Issue targetIssue = issueService.createIssue(
                project1.getId(),
                "Login redirect error",
                "Login redirects to wrong page after valid account",
                tester1.getId(),
                Priority.MAJOR
        );

        List<AssigneeRecommendation> recommendations =
                recommendationService.recommendAssignees(targetIssue.getId(), 3);

        assertFalse(recommendations.isEmpty());

        AssigneeRecommendation first = recommendations.get(0);

        assertEquals(dev1.getId(), first.getAssignee().getId());
        assertTrue(first.getScore() > 0.0);
        assertTrue(first.getMatchedIssueCount() >= 1);
        assertTrue(first.getMatchedTerms().contains("login"));
        assertTrue(first.getEvidenceIssueTitles().contains("Login validation error"));
        assertTrue(first.getExplanation().contains("dev1"));
    }

    @Test
    void unresolvedIssuesAreNotUsedAsRecommendationHistory() {
        Issue unresolvedIssue = issueService.createIssue(
                project1.getId(),
                "Login page layout bug",
                "Login page layout is broken",
                tester1.getId(),
                Priority.MINOR
        );

        issueService.assignIssue(unresolvedIssue.getId(), pl1.getId(), dev1.getId(), "not fixed yet");

        Issue targetIssue = issueService.createIssue(
                project1.getId(),
                "Login button problem",
                "Login button does not respond",
                tester1.getId(),
                Priority.MINOR
        );

        List<AssigneeRecommendation> recommendations =
                recommendationService.recommendAssignees(targetIssue.getId(), 3);

        assertEquals(0, recommendations.size());
    }

    @Test
    void recommendationContainsCurrentWorkloadInformation() {
        Issue loginHistory = issueService.createIssue(
                project1.getId(),
                "Login validation error",
                "Login fails when user submits valid account",
                tester1.getId(),
                Priority.MAJOR
        );

        issueService.assignIssue(loginHistory.getId(), pl1.getId(), dev1.getId(), "please fix login validation");
        issueService.markFixed(loginHistory.getId(), dev1.getId(), "fixed login validation");
        issueService.resolveIssue(loginHistory.getId(), tester1.getId(), "verified");
        issueService.closeIssue(loginHistory.getId(), pl1.getId(), "closed");

        Issue openWork = issueService.createIssue(
                project1.getId(),
                "Notification error",
                "Notification is not delivered",
                tester1.getId(),
                Priority.MAJOR
        );

        issueService.assignIssue(openWork.getId(), pl1.getId(), dev1.getId(), "currently assigned work");

        Issue targetIssue = issueService.createIssue(
                project1.getId(),
                "Login account validation issue",
                "Valid account cannot pass login validation",
                tester1.getId(),
                Priority.MAJOR
        );

        List<AssigneeRecommendation> recommendations =
                recommendationService.recommendAssignees(targetIssue.getId(), 3);

        assertFalse(recommendations.isEmpty());

        AssigneeRecommendation first = recommendations.get(0);

        assertEquals(dev1.getId(), first.getAssignee().getId());
        assertEquals(1, first.getCurrentOpenAssignedIssueCount());
        assertTrue(first.getExplanation().contains("currentOpenAssignedIssues=1"));
    }
}