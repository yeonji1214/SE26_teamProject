package its.service;

import its.domain.issue.Comment;
import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.project.Project;
import its.domain.user.Role;
import its.domain.user.User;
import its.repository.issue.IssueRepository;
import its.repository.project.ProjectRepository;
import its.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class IssueService {
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public IssueService(IssueRepository issueRepository,
                        UserRepository userRepository,
                        ProjectRepository projectRepository) {
        this.issueRepository = Objects.requireNonNull(issueRepository, "issueRepository must not be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
        this.projectRepository = Objects.requireNonNull(projectRepository, "projectRepository must not be null");
    }

    public Issue createIssue(Long projectId,
                             String title,
                             String description,
                             Long reporterId,
                             Priority priority) {
        Project project = findProject(projectId);
        User reporter = findUser(reporterId);
        Issue issue = new Issue(
                null,
                project,
                title,
                description,
                reporter,
                LocalDateTime.now(),
                null,
                null,
                priority == null ? Priority.MAJOR : priority,
                IssueStatus.NEW
        );
        return issueRepository.save(issue);
    }

    public Issue getIssue(Long issueId) {
        return findIssue(issueId);
    }

    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public List<Issue> searchIssues(IssueSearchCriteria criteria) {
        IssueSearchCriteria safeCriteria = criteria == null ? new IssueSearchCriteria() : criteria;
        return issueRepository.findAll().stream()
                .filter(issue -> safeCriteria.getProjectId() == null
                        || issue.getProject().getId().equals(safeCriteria.getProjectId()))
                .filter(issue -> safeCriteria.getReporterId() == null
                        || issue.getReporter().getId().equals(safeCriteria.getReporterId()))
                .filter(issue -> safeCriteria.getAssigneeId() == null
                        || (issue.getAssignee() != null && issue.getAssignee().getId().equals(safeCriteria.getAssigneeId())))
                .filter(issue -> safeCriteria.getStatus() == null
                        || issue.getStatus() == safeCriteria.getStatus())
                .filter(issue -> safeCriteria.getPriority() == null
                        || issue.getPriority() == safeCriteria.getPriority())
                .filter(issue -> containsKeyword(issue, safeCriteria.getKeyword()))
                .collect(Collectors.toList());
    }

    public Issue assignIssue(Long issueId, Long plId, Long assigneeId, String commentContent) {
        User pl = findUser(plId);
        if (!pl.hasRole(Role.PL) && !pl.hasRole(Role.ADMIN)) {
            throw new IllegalArgumentException("only PL or ADMIN can assign issue");
        }

        User assignee = findUser(assigneeId);
        if (!assignee.hasRole(Role.DEV)) {
            throw new IllegalArgumentException("assignee must be DEV");
        }

        Issue issue = findIssue(issueId);
        issue.assignTo(assignee);
        issueRepository.update(issue);

        if (commentContent != null && !commentContent.isBlank()) {
            addComment(issueId, plId, commentContent);
        }
        return issue;
    }

    public Issue markFixed(Long issueId, Long devId, String commentContent) {
        User dev = findUser(devId);
        if (!dev.hasRole(Role.DEV)) {
            throw new IllegalArgumentException("only DEV can fix issue");
        }

        Issue issue = findIssue(issueId);
        if (issue.getAssignee() == null || !issue.getAssignee().getId().equals(devId)) {
            throw new IllegalArgumentException("only assigned DEV can fix issue");
        }

        issue.markFixedBy(dev);
        issueRepository.update(issue);

        if (commentContent != null && !commentContent.isBlank()) {
            addComment(issueId, devId, commentContent);
        }
        return issue;
    }

    public Issue resolveIssue(Long issueId, Long testerId, String commentContent) {
        User tester = findUser(testerId);
        if (!tester.hasRole(Role.TESTER)) {
            throw new IllegalArgumentException("only TESTER can resolve issue");
        }

        Issue issue = findIssue(issueId);
        if (issue.getStatus() != IssueStatus.FIXED) {
            throw new IllegalArgumentException("only FIXED issue can be resolved");
        }

        issue.resolve();
        issueRepository.update(issue);

        if (commentContent != null && !commentContent.isBlank()) {
            addComment(issueId, testerId, commentContent);
        }
        return issue;
    }

    public Issue closeIssue(Long issueId, Long plId, String commentContent) {
        User pl = findUser(plId);
        if (!pl.hasRole(Role.PL) && !pl.hasRole(Role.ADMIN)) {
            throw new IllegalArgumentException("only PL or ADMIN can close issue");
        }

        Issue issue = findIssue(issueId);
        if (issue.getStatus() != IssueStatus.RESOLVED) {
            throw new IllegalArgumentException("only RESOLVED issue can be closed");
        }

        issue.close();
        issueRepository.update(issue);

        if (commentContent != null && !commentContent.isBlank()) {
            addComment(issueId, plId, commentContent);
        }
        return issue;
    }

    public Issue reopenIssue(Long issueId, Long testerId, String commentContent) {
        User tester = findUser(testerId);
        if (!tester.hasRole(Role.TESTER) && !tester.hasRole(Role.PL) && !tester.hasRole(Role.ADMIN)) {
            throw new IllegalArgumentException("only TESTER, PL, or ADMIN can reopen issue");
        }

        Issue issue = findIssue(issueId);
        issue.reopen();
        issueRepository.update(issue);

        if (commentContent != null && !commentContent.isBlank()) {
            addComment(issueId, testerId, commentContent);
        }
        return issue;
    }

    public Comment addComment(Long issueId, Long authorId, String content) {
        findIssue(issueId);
        User author = findUser(authorId);
        Comment comment = new Comment(null, issueId, author, content, LocalDateTime.now());
        issueRepository.addComment(comment);
        return comment;
    }

    public List<Comment> getComments(Long issueId) {
        findIssue(issueId);
        return issueRepository.findCommentsByIssue(issueId);
    }

    private boolean containsKeyword(Issue issue, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return issue.getTitle().toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || issue.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    private Issue findIssue(Long issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("issue not found: " + issueId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));
    }

    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("project not found: " + projectId));
    }
}
