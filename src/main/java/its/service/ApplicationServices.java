package its.service;

public class ApplicationServices {
    private final UserService userService;
    private final ProjectService projectService;
    private final IssueService issueService;
    private final StatisticsService statisticsService;
    private final RecommendationService recommendationService;

    public ApplicationServices(
            UserService userService,
            ProjectService projectService,
            IssueService issueService,
            StatisticsService statisticsService,
            RecommendationService recommendationService
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
        if (statisticsService == null) {
            throw new IllegalArgumentException("statisticsService must not be null");
        }
        if (recommendationService == null) {
            throw new IllegalArgumentException("recommendationService must not be null");
        }

        this.userService = userService;
        this.projectService = projectService;
        this.issueService = issueService;
        this.statisticsService = statisticsService;
        this.recommendationService = recommendationService;
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

    public StatisticsService getStatisticsService() {
        return statisticsService;
    }

    public RecommendationService getRecommendationService() {
        return recommendationService;
    }
}