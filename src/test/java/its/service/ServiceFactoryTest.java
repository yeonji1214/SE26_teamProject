package its.service;

import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.project.Project;
import its.domain.user.Role;
import its.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceFactoryTest {

    @TempDir
    Path tempDir;

    @Test
    void dataIsLoadedAgainWhenServicesAreRecreatedWithSameDatabaseFile() {
        Path databasePath = tempDir.resolve("service-factory-test.db");

        ApplicationServices firstServices = ServiceFactory.createWithSqliteDatabase(databasePath);

        User tester1 = firstServices.getUserService().createUser("tester1", "pw", Role.TESTER);
        Project project1 = firstServices.getProjectService().createProject("project1", "demo project");

        Issue created = firstServices.getIssueService().createIssue(
                project1.getId(),
                "Persist issue",
                "This issue should remain after recreating services",
                tester1.getId(),
                Priority.MAJOR
        );

        ApplicationServices secondServices = ServiceFactory.createWithSqliteDatabase(databasePath);

        Issue loaded = secondServices.getIssueService().getIssue(created.getId());

        assertEquals(created.getId(), loaded.getId());
        assertEquals("Persist issue", loaded.getTitle());
        assertEquals(IssueStatus.NEW, loaded.getStatus());
        assertEquals("tester1", loaded.getReporter().getUsername());
    }
}