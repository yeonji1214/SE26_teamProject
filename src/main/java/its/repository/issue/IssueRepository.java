package its.repository.issue;

import its.domain.issue.Comment;
import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;

import java.util.List;
import java.util.Optional;

public interface IssueRepository {
    Issue save(Issue issue);
    void update(Issue issue);
    Optional<Issue> findById(Long id);
    List<Issue> findAll();
    List<Issue> findByStatus(IssueStatus status);
    void addComment(Comment comment);
    List<Comment> findCommentsByIssue(Long issueId);
}
