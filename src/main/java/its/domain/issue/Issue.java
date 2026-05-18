package its.domain.issue;

import its.domain.project.Project;
import its.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Issue {
    private Long id;
    private Project project;
    private String title;
    private String description;
    private User reporter;
    private LocalDateTime reportedDate;
    private User fixer;
    private User assignee;
    private Priority priority;
    private IssueStatus status;
    private List<Comment> comments = new ArrayList<>();

    public Issue(Long id,
                 Project project,
                 String title,
                 String description,
                 User reporter,
                 LocalDateTime reportedDate,
                 User fixer,
                 User assignee,
                 Priority priority,
                 IssueStatus status) {
        validateTitle(title);
        validateDescription(description);
        this.id = id;
        this.project = Objects.requireNonNull(project, "project must not be null");
        this.title = title;
        this.description = description;
        this.reporter = Objects.requireNonNull(reporter, "reporter must not be null");
        this.reportedDate = Objects.requireNonNull(reportedDate, "reportedDate must not be null");
        this.fixer = fixer;
        this.assignee = assignee;
        this.priority = priority == null ? Priority.MAJOR : priority;
        this.status = status == null ? IssueStatus.NEW : status;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public User getReporter() {
        return reporter;
    }

    public LocalDateTime getReportedDate() {
        return reportedDate;
    }

    public User getFixer() {
        return fixer;
    }

    public User getAssignee() {
        return assignee;
    }

    public Priority getPriority() {
        return priority;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProject(Project project) {
        this.project = Objects.requireNonNull(project, "project must not be null");
    }

    public void setTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void setDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    public void setReporter(User reporter) {
        this.reporter = Objects.requireNonNull(reporter, "reporter must not be null");
    }

    public void setReportedDate(LocalDateTime reportedDate) {
        this.reportedDate = Objects.requireNonNull(reportedDate, "reportedDate must not be null");
    }

    public void setFixer(User fixer) {
        this.fixer = fixer;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public void setPriority(Priority priority) {
        this.priority = priority == null ? Priority.MAJOR : priority;
    }

    public void setStatus(IssueStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments == null ? new ArrayList<>() : new ArrayList<>(comments);
    }

    public void addComment(Comment comment) {
        comments.add(Objects.requireNonNull(comment, "comment must not be null"));
    }

    public void assignTo(User assignee) {
        this.assignee = Objects.requireNonNull(assignee, "assignee must not be null");
        this.status = IssueStatus.ASSIGNED;
    }

    public void markFixedBy(User fixer) {
        this.fixer = Objects.requireNonNull(fixer, "fixer must not be null");
        this.status = IssueStatus.FIXED;
    }

    public void resolve() {
        this.status = IssueStatus.RESOLVED;
    }

    public void close() {
        this.status = IssueStatus.CLOSED;
    }

    public void reopen() {
        this.status = IssueStatus.REOPENED;
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("issue title must not be blank");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("issue description must not be blank");
        }
    }
}
