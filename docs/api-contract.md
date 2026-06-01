# HTTP API Contract

## Base URL

```text
http://localhost:8080
```

Run API server:

```powershell
.\gradlew.bat run --args="api"
```

## Users

### Get users

```text
GET /api/users
```

### Get user

```text
GET /api/users/{id}
```

### Create user

```text
POST /api/users
```

Request:

```json
{
  "username": "tester1",
  "password": "pw",
  "role": "TESTER"
}
```

### Login as user

```text
POST /api/login
```

Request:

```json
{
  "userId": 1
}
```

## Projects

### Get projects

```text
GET /api/projects
```

### Get project

```text
GET /api/projects/{id}
```

### Create project

```text
POST /api/projects
```

Request:

```json
{
  "name": "project1",
  "description": "Default demo project"
}
```

## Issues

### Get issues

```text
GET /api/issues
```

Optional query parameters:

```text
projectId
reporterId
assigneeId
status
priority
keyword
```

Example:

```text
GET /api/issues?status=NEW&keyword=login
```

### Get issue detail

```text
GET /api/issues/{id}
```

### Create issue

```text
POST /api/issues
```

Request:

```json
{
  "projectId": 1,
  "title": "Login error",
  "description": "Login fails with valid account",
  "priority": "MAJOR",
  "reporterId": 6
}
```

### Update issue status

```text
PATCH /api/issues/{id}/status
```

Assign request:

```json
{
  "status": "ASSIGNED",
  "actorId": 2,
  "assigneeId": 4,
  "comment": "Assign to dev1"
}
```

Fixed request:

```json
{
  "status": "FIXED",
  "actorId": 4,
  "comment": "Fixed login validation"
}
```

Resolved request:

```json
{
  "status": "RESOLVED",
  "actorId": 6,
  "comment": "Verified by tester"
}
```

Closed request:

```json
{
  "status": "CLOSED",
  "actorId": 2,
  "comment": "Closed by PL"
}
```

Reopened request:

```json
{
  "status": "REOPENED",
  "actorId": 6,
  "comment": "Issue still occurs"
}
```

### Add comment

```text
POST /api/issues/{id}/comments
```

Request:

```json
{
  "authorId": 6,
  "content": "I checked this issue."
}
```

## Statistics

```text
GET /api/statistics
```

Response includes:

```text
totalIssues
byStatus
byPriority
byDay
byMonth
byAssignee
```

## Recommendations

```text
GET /api/issues/{id}/recommendations
GET /api/issues/{id}/recommendations?limit=3
```

Response includes:

```text
assignee
score
matchedIssueCount
matchedTerms
evidenceIssueTitles
currentOpenAssignedIssueCount
explanation
```

## Frontend Integration Note

React code should replace mock API functions with `fetch()` calls to the endpoints above.

The Java API layer calls:

- UserService
- ProjectService
- IssueService
- StatisticsService
- RecommendationService

React must not access SQLite or repository classes directly.