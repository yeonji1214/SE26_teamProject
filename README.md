# SE26 Issue Tracker Team Project

## Project Structure

```text
src/main/java/its
├─ domain
├─ repository
├─ service
└─ ui
```

## Backend Integration

UI developers should read:

- `docs/backend-integration-guide.md`
- `docs/ui-integration-checklist.md`

The UI layer should access backend features through `ServiceFactory` and `ApplicationServices`.

```java
ApplicationServices services =
        ServiceFactory.createWithSqliteDatabase(Path.of("issue-tracker.db"));
```

## Run Tests

```powershell
.\gradlew.bat test
```

## Run Application

```powershell
.\gradlew.bat run
```