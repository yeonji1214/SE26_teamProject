package its.service;

import its.repository.issue.IssueRepository;
import its.repository.jdbc.DatabaseInitializer;
import its.repository.jdbc.DatabaseManager;
import its.repository.jdbc.JdbcIssueRepository;
import its.repository.jdbc.JdbcProjectRepository;
import its.repository.jdbc.JdbcUserRepository;
import its.repository.project.ProjectRepository;
import its.repository.user.UserRepository;

import java.nio.file.Path;

public class ServiceFactory {

    private ServiceFactory() {
    }

    public static ApplicationServices createWithSqliteDatabase(Path databasePath) {
        DatabaseManager databaseManager = DatabaseManager.fileDatabase(databasePath);
        new DatabaseInitializer(databaseManager).initialize();

        UserRepository userRepository = new JdbcUserRepository(databaseManager);
        ProjectRepository projectRepository = new JdbcProjectRepository(databaseManager);
        IssueRepository issueRepository = new JdbcIssueRepository(databaseManager);

        UserService userService = new UserService(userRepository);
        ProjectService projectService = new ProjectService(projectRepository);
        IssueService issueService = new IssueService(issueRepository, userRepository, projectRepository);
        StatisticsService statisticsService = new StatisticsService(issueRepository);
        RecommendationService recommendationService = new RecommendationService(issueRepository);

        return new ApplicationServices(
                userService,
                projectService,
                issueService,
                statisticsService,
                recommendationService
        );
    }
}