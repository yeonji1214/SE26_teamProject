package its.service;

import its.domain.issue.Issue;
import its.domain.issue.Priority;
import its.domain.project.Project;
import its.domain.user.Role;
import its.domain.user.User;

public class DemoDataSeeder {
    private final UserService userService;
    private final ProjectService projectService;
    private final IssueService issueService;

    public DemoDataSeeder(ApplicationServices services) {
        if (services == null) {
            throw new IllegalArgumentException("services must not be null");
        }

        this.userService = services.getUserService();
        this.projectService = services.getProjectService();
        this.issueService = services.getIssueService();
    }

    public void seedIfEmpty() {
        if (!userService.getAllUsers().isEmpty()) {
            return;
        }

        userService.createUser("admin", "pw", Role.ADMIN);
        User pl1 = userService.createUser("PL1", "pw", Role.PL);
        userService.createUser("PL2", "pw", Role.PL);

        User dev1 = null;
        User dev2 = null;

        for (int i = 1; i <= 10; i++) {
            User dev = userService.createUser("dev" + i, "pw", Role.DEV);
            if (i == 1) {
                dev1 = dev;
            }
            if (i == 2) {
                dev2 = dev;
            }
        }

        User tester1 = null;

        for (int i = 1; i <= 5; i++) {
            User tester = userService.createUser("tester" + i, "pw", Role.TESTER);
            if (i == 1) {
                tester1 = tester;
            }
        }

        Project project1 = projectService.createProject("project1", "Default demo project");

        if (tester1 != null && dev1 != null && dev2 != null) {
            Issue loginHistory = issueService.createIssue(
                    project1.getId(),
                    "Login validation error",
                    "Login fails when user submits valid account",
                    tester1.getId(),
                    Priority.MAJOR
            );

            issueService.assignIssue(loginHistory.getId(), pl1.getId(), dev1.getId(), "please fix login validation");
            issueService.markFixed(loginHistory.getId(), dev1.getId(), "fixed login validation");
            issueService.resolveIssue(loginHistory.getId(), tester1.getId(), "verified by tester");
            issueService.closeIssue(loginHistory.getId(), pl1.getId(), "closed after verification");

            Issue saveHistory = issueService.createIssue(
                    project1.getId(),
                    "Database save failure",
                    "Issue description is not saved into database",
                    tester1.getId(),
                    Priority.CRITICAL
            );

            issueService.assignIssue(saveHistory.getId(), pl1.getId(), dev2.getId(), "please fix database save");
            issueService.markFixed(saveHistory.getId(), dev2.getId(), "fixed database save logic");
            issueService.resolveIssue(saveHistory.getId(), tester1.getId(), "save feature verified");

            issueService.createIssue(
                    project1.getId(),
                    "Login redirect error",
                    "Login redirects to wrong page after valid account",
                    tester1.getId(),
                    Priority.MAJOR
            );

            issueService.createIssue(
                    project1.getId(),
                    "Crash on save",
                    "Application crashes when saving issue description",
                    tester1.getId(),
                    Priority.BLOCKER
            );
        }
    }
}