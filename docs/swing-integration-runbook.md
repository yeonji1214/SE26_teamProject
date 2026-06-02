# Swing Integration Runbook

## 1. Purpose

This document explains how the Swing UI is connected to the Java backend service layer.

The goal of this integration is to make the Swing UI reuse the same domain, service, repository, and persistence layers as the console demo and HTTP API.

## 2. Execution Modes

The application supports three execution modes.

```powershell
.\gradlew.bat run
```

Runs the console demo.

```powershell
.\gradlew.bat run --args="api"
```

Runs the Java HTTP API server.

```powershell
.\gradlew.bat run --args="swing"
```

Runs the Swing GUI application.

## 3. Demo Accounts

Demo data is seeded through `DemoDataSeeder`.

The following accounts can be used for manual testing.

| Username | Password | Role |
|---|---|---|
| admin | pw | ADMIN |
| PL1 | pw | PL |
| PL2 | pw | PL |
| dev1 | pw | DEV |
| dev2 | pw | DEV |
| tester1 | pw | TESTER |

## 4. Architecture

The Swing UI does not directly access JDBC repositories or SQLite.

Instead, it uses the service layer through `ApplicationServices`.

```text
Swing UI
↓
ApplicationServices
↓
UserService / ProjectService / IssueService / StatisticsService / RecommendationService
↓
Repository Interface
↓
SQLite JDBC Repository
↓
SQLite Database
```

This keeps the UI layer separated from business rules and persistence details.

## 5. Connected Swing Screens

### 5.1 LoginPanel

`LoginPanel` is connected to `UserService`.

Responsibilities:

- receive username and password
- find the user through `UserService`
- validate the password
- pass the logged-in `User` object to `MainFrame`

Before this integration, the login screen accepted arbitrary text.
After this integration, only registered demo users can log in.

### 5.2 ProjectsPanel

`ProjectsPanel` is connected to `ProjectService`.

Responsibilities:

- load projects from the backend service
- display project name and description
- refresh the project table when the screen is activated

The old dummy project list was removed.

### 5.3 IssuesPanel

`IssuesPanel` is connected to `IssueService` and `ProjectService`.

Responsibilities:

- load issue list from the backend service
- display issue ID, title, status, priority, reporter, assignee, and reported date
- filter issues by project, status, priority, reporter, assignee, and title keyword
- open the selected issue detail screen
- move to the create issue screen

The old dummy issue list was removed.

### 5.4 CreateIssuePanel

`CreateIssuePanel` is connected to `ProjectService`.

Responsibilities:

- load project names from the backend service
- receive issue title, description, and priority
- pass the create request to `MainFrame`

The actual creation is handled by `MainFrame` through `IssueService.createIssue`.

The current logged-in user is used as the issue reporter.

### 5.5 IssueDetailPanel

`IssueDetailPanel` is connected to `IssueService`.

Responsibilities:

- load issue detail by issue ID
- display project, reporter, assignee, fixer, status, priority, reported date, title, and description
- display comments
- request status changes

Status changes are not handled inside the panel.
The panel only sends user actions to `MainFrame`.

### 5.6 StatisticsPanel

`StatisticsPanel` is connected to `StatisticsService`.

Responsibilities:

- display total issue count
- display open issue count
- display resolved or closed issue count
- display high-priority issue count
- display statistics by status, priority, day, month, and assignee

The statistics screen is refreshed when the screen is activated.
It is also refreshed after issue creation and status changes.

## 6. Status Change Rules

Status transition rules are enforced by `IssueService`.

The Swing UI does not duplicate these business rules.

Examples:

| Target Status | Service Method | Main Rule |
|---|---|---|
| ASSIGNED | `assignIssue` | PL or ADMIN assigns a DEV |
| FIXED | `markFixed` | assigned DEV marks the issue as fixed |
| RESOLVED | `resolveIssue` | TESTER resolves the issue |
| CLOSED | `closeIssue` | PL or ADMIN closes the issue |
| REOPENED | `reopenIssue` | TESTER, PL, or ADMIN reopens the issue |

If a user violates a rule, the service throws an exception.
The Swing UI catches the exception and displays it in an error dialog.

## 7. Assignee Recommendation Integration

When changing an issue to `ASSIGNED`, `MainFrame` uses `RecommendationService`.

Current behavior:

1. request the top recommended assignee
2. assign the issue to that developer
3. if no recommendation exists, fall back to the first DEV user

This allows the Swing UI to use the recommendation feature without directly implementing recommendation logic inside the UI.

## 8. Manual Test Checklist

### 8.1 Login

```text
tester1 / pw
PL1 / pw
dev1 / pw
```

Expected result:

- valid accounts can log in
- invalid accounts cannot log in

### 8.2 Project List

Expected result:

- `ProjectsPanel` shows `project1`
- dummy project rows do not appear

### 8.3 Issue List

Expected result:

- `IssuesPanel` shows persisted issues from SQLite
- filters work for status, priority, project, reporter, assignee, and title keyword

### 8.4 Issue Creation

Steps:

1. log in as `tester1`
2. open `Create Issue`
3. select `project1`
4. enter title and description
5. select priority
6. save

Expected result:

- success dialog appears
- new issue appears in `IssuesPanel`
- new issue remains after restarting the application

### 8.5 Issue Detail

Steps:

1. open `Issues`
2. click an issue row

Expected result:

- detail screen opens
- actual issue title, description, status, priority, reporter, assignee, and comments are shown

### 8.6 Status Change

Examples:

- `tester1` changing NEW to ASSIGNED should fail
- `PL1` changing NEW to ASSIGNED should succeed
- role violations should show an error dialog

### 8.7 Statistics

Expected result:

- total issue count is displayed
- status and priority counts are displayed
- daily and monthly counts are displayed
- assignee counts are displayed
- statistics update after issue creation or status change

## 9. Design Notes

This integration follows a layered architecture.

```text
UI → Service → Repository → SQLite
```

The Swing UI only depends on service classes.
It does not know whether the repository is in-memory, JDBC, or another implementation.

This makes it possible to reuse the same backend logic from:

- console demo
- Java HTTP API
- React Web UI
- Swing UI

## 10. Remaining Limitations

The following items are intentionally left as future work.

- Korean text may appear as boxes on some Windows Swing environments because of font configuration.
- Issue edit and delete buttons currently show a not-supported dialog.
- The Swing UI uses simple tables and forms rather than advanced layout components.
- Status transition selection is manual and may still show invalid target statuses.
- Assignee selection is automatic through recommendation logic instead of manual user selection.