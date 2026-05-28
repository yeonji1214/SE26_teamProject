package its.service;

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
        userService.createUser("PL1", "pw", Role.PL);
        userService.createUser("PL2", "pw", Role.PL);

        User dev1 = null;
        for (int i = 1; i <= 10; i++) {
            User dev = userService.createUser("dev" + i, "pw", Role.DEV);
            if (i == 1) {
                dev1 = dev;
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

        if (tester1 != null && dev1 != null) {
            issueService.createIssue(
                    project1.getId(),
                    "Login page error",
                    "Login page fails when tester submits valid account",
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