# UI Integration Checklist

## Common Rule

- [ ] UI code gets services from `ApplicationServices`.
- [ ] UI code does not create JDBC repositories directly.
- [ ] UI code does not initialize the database directly.
- [ ] Business rules are handled by Service classes.
- [ ] UI code only handles screen rendering and user input.

## Required Service Entry Point

```java
ApplicationServices services =
        ServiceFactory.createWithSqliteDatabase(Path.of("issue-tracker.db"));
```

## Issue List Screen

- [ ] Use `IssueService.searchIssues(criteria)`.
- [ ] Do not filter issue status manually in UI if `IssueSearchCriteria` can handle it.
- [ ] Show title, priority, status, reporter, assignee.

## Issue Detail Screen

- [ ] Use `IssueService.getIssue(issueId)`.
- [ ] Use `IssueService.getComments(issueId)`.
- [ ] Show comment history in chronological order.

## Issue Create Screen

- [ ] Use `IssueService.createIssue(...)`.
- [ ] Do not manually set status to NEW in UI.
- [ ] Do not manually set reported date in UI.

## Assign Issue Screen

- [ ] Use `IssueService.assignIssue(...)`.
- [ ] Show recommended assignees from `RecommendationService`.
- [ ] Catch `IllegalArgumentException` and show an error message.

## Statistics Screen

- [ ] Use `StatisticsService.countIssuesByStatus()`.
- [ ] Use `StatisticsService.countIssuesByPriority()`.
- [ ] Use `StatisticsService.countIssuesByDay()`.
- [ ] Use `StatisticsService.countIssuesByMonth()`.
- [ ] Use `StatisticsService.countIssuesByAssignee()`.

## Recommendation UI

- [ ] Use `RecommendationService.recommendAssignees(issueId, 3)`.
- [ ] Show score.
- [ ] Show matched terms.
- [ ] Show evidence issue titles.
- [ ] Show current open assigned issue count.