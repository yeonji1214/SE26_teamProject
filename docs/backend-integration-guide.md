# Backend Integration Guide for UI Developers

## 1. Purpose

This document explains how Swing, JavaFX, and future Web/API layers should use the backend core of the Issue Tracker project.

The main design rule is:

```text
UI
↓
Service
↓
Repository Interface
↓
Repository Implementation
↓
Persistent Storage
```

UI code must not directly access JDBC repositories, database initialization code, or SQL details.
UI code should call service classes only.

---

## 2. Current Backend Entry Point

The recommended entry point for UI developers is `ServiceFactory`.

```java
import its.service.ApplicationServices;
import its.service.ServiceFactory;

import java.nio.file.Path;

ApplicationServices services =
        ServiceFactory.createWithSqliteDatabase(Path.of("issue-tracker.db"));
```

After creating `ApplicationServices`, UI code can access each service:

```java
UserService userService = services.getUserService();
ProjectService projectService = services.getProjectService();
IssueService issueService = services.getIssueService();
StatisticsService statisticsService = services.getStatisticsService();
RecommendationService recommendationService = services.getRecommendationService();
```

---

## 3. Do Not Access Repository Directly from UI

UI code should not do this:

```java
DatabaseManager databaseManager = DatabaseManager.fileDatabase(Path.of("issue-tracker.db"));
IssueRepository issueRepository = new JdbcIssueRepository(databaseManager);
```

This is not recommended because it exposes database and repository implementation details to the UI layer.

Instead, UI code should do this:

```java
ApplicationServices services =
        ServiceFactory.createWithSqliteDatabase(Path.of("issue-tracker.db"));

IssueService issueService = services.getIssueService();
```

This keeps the UI layer independent from the storage implementation.

---

## 4. Why This Matters

The project requires multiple UI implementations, such as Swing, JavaFX, or Web UI.
All UI implementations should reuse the same backend logic.

This design has the following advantages:

* Swing UI and JavaFX UI can use the same `IssueService`.
* Storage can be changed from InMemory Repository to JDBC Repository without changing UI logic.
* Business rules such as issue assignment, fixing, resolving, closing, statistics, and recommendation remain in the Service layer.
* UI code only handles input/output and screen rendering.

---

## 5. Basic Service Initialization Example

For a desktop UI, initialize backend services once when the application starts.

```java
public class AppContext {
    private static final ApplicationServices services =
            ServiceFactory.createWithSqliteDatabase(Path.of("issue-tracker.db"));

    private AppContext() {
    }

    public static ApplicationServices getServices() {
        return services;
    }
}
```

Then each UI screen can reuse the same services.

```java
IssueService issueService = AppContext.getServices().getIssueService();
```

---

## 6. User Operations

### Create User

```java
User tester = services.getUserService().createUser("tester1", "pw", Role.TESTER);
User pl = services.getUserService().createUser("PL1", "pw", Role.PL);
User dev = services.getUserService().createUser("dev1", "pw", Role.DEV);
```

### Load All Users

```java
List<User> users = services.getUserService().getAllUsers();
```

### Find User by Username

```java
User tester = services.getUserService().getUserByUsername("tester1");
```

---

## 7. Project Operations

### Create Project

```java
Project project = services.getProjectService()
        .createProject("project1", "Default demo project");
```

### Load All Projects

```java
List<Project> projects = services.getProjectService().getAllProjects();
```

---

## 8. Issue Operations

### Create Issue

```java
Issue issue = services.getIssueService().createIssue(
        project.getId(),
        "Login error",
        "Login fails with valid account",
        tester.getId(),
        Priority.MAJOR
);
```

When an issue is created:

* status is set to `NEW`
* reporter is set to the current user
* reported date is set automatically
* priority defaults to `MAJOR` if null is passed

---

### Search Issues

```java
IssueSearchCriteria criteria = new IssueSearchCriteria()
        .setProjectId(project.getId())
        .setStatus(IssueStatus.NEW)
        .setKeyword("login");

List<Issue> issues = services.getIssueService().searchIssues(criteria);
```

Search criteria can include:

* project id
* reporter id
* assignee id
* issue status
* priority
* keyword

---

### View Issue Detail

```java
Issue issue = services.getIssueService().getIssue(issueId);
List<Comment> comments = services.getIssueService().getComments(issueId);
```

The detail screen should display:

* title
* description
* reporter
* reported date
* priority
* status
* assignee
* fixer
* comments

---

### Add Comment

```java
services.getIssueService().addComment(
        issueId,
        authorId,
        "I checked this issue."
);
```

---

## 9. Issue Lifecycle Operations

### Assign Issue

Only PL or ADMIN should assign an issue.

```java
services.getIssueService().assignIssue(
        issueId,
        plId,
        devId,
        "Please fix this issue."
);
```

Expected result:

```text
NEW or REOPENED → ASSIGNED
assignee = selected developer
```

---

### Mark Issue as Fixed

Only the assigned DEV should mark an issue as fixed.

```java
services.getIssueService().markFixed(
        issueId,
        devId,
        "Fixed login validation logic."
);
```

Expected result:

```text
ASSIGNED → FIXED
fixer = assigned developer
```

---

### Resolve Issue

Only TESTER should resolve a fixed issue.

```java
services.getIssueService().resolveIssue(
        issueId,
        testerId,
        "Verified by tester."
);
```

Expected result:

```text
FIXED → RESOLVED
```

---

### Close Issue

Only PL or ADMIN should close a resolved issue.

```java
services.getIssueService().closeIssue(
        issueId,
        plId,
        "Closed after verification."
);
```

Expected result:

```text
RESOLVED → CLOSED
```

---

### Reopen Issue

TESTER, PL, or ADMIN can reopen an issue.

```java
services.getIssueService().reopenIssue(
        issueId,
        testerId,
        "The issue still occurs."
);
```

Expected result:

```text
CLOSED or RESOLVED or FIXED → REOPENED
```

---

## 10. Statistics Operations

UI should use `StatisticsService` for chart or summary screens.

```java
StatisticsService statisticsService = services.getStatisticsService();

Map<IssueStatus, Long> byStatus = statisticsService.countIssuesByStatus();
Map<Priority, Long> byPriority = statisticsService.countIssuesByPriority();
Map<LocalDate, Long> byDay = statisticsService.countIssuesByDay();
Map<YearMonth, Long> byMonth = statisticsService.countIssuesByMonth();
Map<String, Long> byAssignee = statisticsService.countIssuesByAssignee();
long total = statisticsService.countTotalIssues();
```

Recommended UI components:

* status count table
* priority count table
* daily issue trend chart
* monthly issue trend chart
* assignee workload summary

---

## 11. Assignee Recommendation Operations

UI should use `RecommendationService` when PL views a new issue or assigns an issue.

```java
List<AssigneeRecommendation> recommendations =
        services.getRecommendationService().recommendAssignees(issueId, 3);
```

Each recommendation includes:

```java
recommendation.getAssignee();
recommendation.getScore();
recommendation.getMatchedIssueCount();
recommendation.getMatchedTerms();
recommendation.getEvidenceIssueTitles();
recommendation.getCurrentOpenAssignedIssueCount();
recommendation.getExplanation();
```

The UI can display:

```text
Recommended developer: dev1
Score: 82.4
Matched terms: login, validation, account
Evidence issues: Login validation error
Current open assigned issues: 1
```

This makes the recommendation explainable instead of showing only a developer name.

---

## 12. Swing / JavaFX Integration Rule

Swing and JavaFX screens may directly call Service classes because they run in the same Java application.

Recommended flow:

```text
Button click
↓
UI event handler
↓
IssueService / StatisticsService / RecommendationService
↓
Update table, panel, or chart
```

Example:

```java
assignButton.addActionListener(event -> {
    issueService.assignIssue(issueId, plId, devId, "Assigned from UI");
    refreshIssueTable();
});
```

---

## 13. React Web UI Integration Rule

React code cannot directly call Java service classes because React runs in the browser.

React should eventually call HTTP API endpoints.
The future Java API layer should call the existing Service classes.

Recommended future structure:

```text
React Page
↓ fetch()
Java API Controller
↓
IssueService / StatisticsService / RecommendationService
↓
Repository
↓
SQLite
```

Example future endpoints:

```text
GET    /api/issues
GET    /api/issues/{id}
POST   /api/issues
POST   /api/issues/{id}/comments
POST   /api/issues/{id}/assign
POST   /api/issues/{id}/fix
POST   /api/issues/{id}/resolve
POST   /api/issues/{id}/close
GET    /api/statistics
GET    /api/issues/{id}/recommendations
```

Until the Java API layer is implemented, React UI can use mock data or static sample data.

---

## 14. UI Developer Checklist

Before connecting a screen, check the following:

* Do I get services from `ServiceFactory` or `ApplicationServices`?
* Am I avoiding direct use of `JdbcRepository` classes?
* Am I avoiding direct SQL or database initialization in UI code?
* Is business logic handled by Service classes?
* Does the UI only handle rendering and user input?
* Do I refresh the screen after calling a Service method?
* Do I show validation errors from Service exceptions in a user-friendly way?

---

## 15. Error Handling Guideline

Service methods may throw `IllegalArgumentException` when a rule is violated.

Examples:

* assigning an issue to a non-DEV user
* non-PL user trying to assign an issue
* non-TESTER user trying to resolve an issue
* closing an issue that is not resolved
* duplicated username
* duplicated project name

UI should catch these exceptions and show a message dialog or error banner.

Example:

```java
try {
    issueService.assignIssue(issueId, plId, devId, "Assigned");
} catch (IllegalArgumentException e) {
    JOptionPane.showMessageDialog(null, e.getMessage());
}
```

---

## 16. Summary

UI developers should use the backend through this entry point:

```java
ApplicationServices services =
        ServiceFactory.createWithSqliteDatabase(Path.of("issue-tracker.db"));
```

The UI layer should call:

* `UserService`
* `ProjectService`
* `IssueService`
* `StatisticsService`
* `RecommendationService`

The UI layer should not directly call:

* `JdbcUserRepository`
* `JdbcProjectRepository`
* `JdbcIssueRepository`
* `DatabaseManager`
* `DatabaseInitializer`

This preserves the project’s layered architecture and supports multiple UI implementations.
