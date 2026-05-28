package its.service;

public class ApplicationServices {
    private final UserService userService;
    private final ProjectService projectService;
    private final IssueService issueService;

    public ApplicationServices(
            UserService userService,
            ProjectService projectService,
            IssueService issueService
    ) {
        if (userService == null) {
            throw new IllegalArgumentException("userService must not be null");
        }
        if (projectService == null) {
            throw new IllegalArgumentException("projectService must not be null");
        }
        if (issueService == null) {
            throw new IllegalArgumentException("issueService must not be null");
        }

        this.userService = userService;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    public UserService getUserService() {
        return userService;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public IssueService getIssueService() {
        return issueService;
    }
}