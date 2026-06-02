package its;

import its.api.ApiServer;
import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.service.ApplicationServices;
import its.service.AssigneeRecommendation;
import its.service.DemoDataSeeder;
import its.service.ServiceFactory;
import its.ui.gui.MainFrame;
import its.ui.gui.common.UIConfig;

import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class Main {
    private static final Path DATABASE_PATH = Path.of("issue-tracker.db");

    public static void main(String[] args) {
        ApplicationServices services = ServiceFactory.createWithSqliteDatabase(DATABASE_PATH);
        new DemoDataSeeder(services).seedIfEmpty();

        if (args.length > 0 && "api".equalsIgnoreCase(args[0])) {
            startApiServer(services);
            return;
        }

        if (args.length > 0 && "swing".equalsIgnoreCase(args[0])) {
            startSwingApp(services);
            return;
        }

        printConsoleDemo(services);
    }

    private static void startApiServer(ApplicationServices services) {
        ApiServer apiServer = ApiServer.create(8080, services);
        apiServer.start();

        System.out.println("Use Ctrl+C to stop the API server.");
    }

    private static void startSwingApp(ApplicationServices services) {
        UIConfig.initialize();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(services);
            frame.setVisible(true);
        });
    }

    private static void printConsoleDemo(ApplicationServices services) {
        System.out.println("Issue Tracker backend initialized.");
        System.out.println("Database file: " + DATABASE_PATH.toAbsolutePath());
        System.out.println("Users: " + services.getUserService().getAllUsers().size());
        System.out.println("Projects: " + services.getProjectService().getAllProjects().size());
        System.out.println("Issues: " + services.getIssueService().getAllIssues().size());

        System.out.println();
        System.out.println("[Statistics]");
        System.out.println("By status: " + services.getStatisticsService().countIssuesByStatus());
        System.out.println("By priority: " + services.getStatisticsService().countIssuesByPriority());
        System.out.println("By day: " + services.getStatisticsService().countIssuesByDay());
        System.out.println("By month: " + services.getStatisticsService().countIssuesByMonth());
        System.out.println("By assignee: " + services.getStatisticsService().countIssuesByAssignee());

        Optional<Issue> targetIssue = findFirstNewIssue(services);

        if (targetIssue.isPresent()) {
            printRecommendation(services, targetIssue.get());
        }
    }

    private static Optional<Issue> findFirstNewIssue(ApplicationServices services) {
        return services.getIssueService().getAllIssues().stream()
                .filter(issue -> issue.getStatus() == IssueStatus.NEW)
                .findFirst();
    }

    private static void printRecommendation(ApplicationServices services, Issue issue) {
        System.out.println();
        System.out.println("[Recommendation]");
        System.out.println("Target issue: #" + issue.getId() + " " + issue.getTitle());

        List<AssigneeRecommendation> recommendations =
                services.getRecommendationService().recommendAssignees(issue.getId(), 3);

        if (recommendations.isEmpty()) {
            System.out.println("No recommendation available.");
            return;
        }

        for (int i = 0; i < recommendations.size(); i++) {
            AssigneeRecommendation recommendation = recommendations.get(i);
            System.out.println((i + 1) + ". "
                    + recommendation.getAssignee().getUsername()
                    + " score=" + recommendation.getScore()
                    + ", matchedTerms=" + recommendation.getMatchedTerms()
                    + ", evidence=" + recommendation.getEvidenceIssueTitles()
                    + ", openAssignedIssues=" + recommendation.getCurrentOpenAssignedIssueCount());
        }
    }
}