package its.repository.issue;

import its.domain.issue.Comment;
import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryIssueRepository implements IssueRepository {
    private final Map<Long, Issue> issues = new LinkedHashMap<>();
    private long issueSequence = 0L;
    private long commentSequence = 0L;

    @Override
    public Issue save(Issue issue) {
        if (issue.getId() == null) {
            issue.setId(++issueSequence);
        } else if (issue.getId() > issueSequence) {
            issueSequence = issue.getId();
        }
        issues.put(issue.getId(), issue);
        return issue;
    }

    @Override
    public void update(Issue issue) {
        if (issue.getId() == null || !issues.containsKey(issue.getId())) {
            throw new IllegalArgumentException("issue not found: " + issue.getId());
        }
        issues.put(issue.getId(), issue);
    }

    @Override
    public Optional<Issue> findById(Long id) {
        return Optional.ofNullable(issues.get(id));
    }

    @Override
    public List<Issue> findAll() {
        return new ArrayList<>(issues.values());
    }

    @Override
    public List<Issue> findByStatus(IssueStatus status) {
        return issues.values().stream()
                .filter(issue -> issue.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void addComment(Comment comment) {
        Issue issue = findById(comment.getIssueId())
                .orElseThrow(() -> new IllegalArgumentException("issue not found: " + comment.getIssueId()));
        if (comment.getId() == null) {
            comment.setId(++commentSequence);
        } else if (comment.getId() > commentSequence) {
            commentSequence = comment.getId();
        }
        issue.addComment(comment);
    }

    @Override
    public List<Comment> findCommentsByIssue(Long issueId) {
        return findById(issueId)
                .map(issue -> new ArrayList<>(issue.getComments()))
                .orElseGet(ArrayList::new);
    }
}
