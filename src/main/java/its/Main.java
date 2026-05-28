package its;

import its.service.ApplicationServices;
import its.service.DemoDataSeeder;
import its.service.ServiceFactory;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path databasePath = Path.of("issue-tracker.db");

        ApplicationServices services = ServiceFactory.createWithSqliteDatabase(databasePath);
        new DemoDataSeeder(services).seedIfEmpty();

        System.out.println("Issue Tracker backend initialized.");
        System.out.println("Database file: " + databasePath.toAbsolutePath());
        System.out.println("Users: " + services.getUserService().getAllUsers().size());
        System.out.println("Projects: " + services.getProjectService().getAllProjects().size());
        System.out.println("Issues: " + services.getIssueService().getAllIssues().size());
    }
}