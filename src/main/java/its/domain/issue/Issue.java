package its.domain.issue;

import its.domain.project.Project;
import its.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public Issue(Long id, Project project, String title, String description, User reporter, LocalDateTime reportedDate, User fixer, User assignee, Priority priority, IssueStatus status) {
        this.id = id;
        this.project = project;
        this.title = title;
        this.description = description;
        this.reporter = reporter;
        this.reportedDate = reportedDate;
        this.fixer = fixer;
        this.assignee = assignee;
        this.priority = priority;
        this.status = status;
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
        return comments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public void setReportedDate(LocalDateTime reportedDate) {
        this.reportedDate = reportedDate;
    }

    public void setFixer(User fixer) {
        this.fixer = fixer;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}

