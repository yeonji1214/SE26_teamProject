package its.api.dto;

import its.domain.issue.Comment;
import its.domain.issue.Issue;
import its.domain.project.Project;
import its.domain.user.User;
import its.service.AssigneeRecommendation;
import its.service.StatisticsService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiDtoMapper {

    private ApiDtoMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    public static ProjectResponse toProjectResponse(Project project) {
        if (project == null) {
            return null;
        }

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription()
        );
    }

    public static CommentResponse toCommentResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        return new CommentResponse(
                comment.getId(),
                comment.getIssueId(),
                toUserResponse(comment.getAuthor()),
                comment.getContent(),
                comment.getCreatedAt().toString()
        );
    }

    public static List<CommentResponse> toCommentResponses(List<?> comments) {
        List<CommentResponse> responses = new ArrayList<>();

        if (comments == null) {
            return responses;
        }

        for (Object item : comments) {
            if (item instanceof Comment comment) {
                responses.add(toCommentResponse(comment));
            }
        }

        return responses;
    }

    public static IssueResponse toIssueResponse(Issue issue) {
        if (issue == null) {
            return null;
        }

        Long projectId = issue.getProject() == null ? null : issue.getProject().getId();

        return new IssueResponse(
                issue.getId(),
                projectId,
                issue.getTitle(),
                issue.getDescription(),
                toUserResponse(issue.getReporter()),
                toUserResponse(issue.getAssignee()),
                toUserResponse(issue.getFixer()),
                issue.getPriority().name(),
                issue.getStatus().name(),
                issue.getReportedDate().toString(),
                toCommentResponses(issue.getComments())
        );
    }

    public static List<IssueResponse> toIssueResponses(List<Issue> issues) {
        List<IssueResponse> responses = new ArrayList<>();

        if (issues == null) {
            return responses;
        }

        for (Issue issue : issues) {
            responses.add(toIssueResponse(issue));
        }

        return responses;
    }

    public static StatisticsResponse toStatisticsResponse(StatisticsService statisticsService) {
        if (statisticsService == null) {
            throw new IllegalArgumentException("statisticsService must not be null");
        }

        return new StatisticsResponse(
                statisticsService.countTotalIssues(),
                enumKeyMapToStringMap(statisticsService.countIssuesByStatus()),
                enumKeyMapToStringMap(statisticsService.countIssuesByPriority()),
                localDateMapToStringMap(statisticsService.countIssuesByDay()),
                yearMonthMapToStringMap(statisticsService.countIssuesByMonth()),
                stringKeyMapToStringMap(statisticsService.countIssuesByAssignee())
        );
    }

    public static RecommendationResponse toRecommendationResponse(AssigneeRecommendation recommendation) {
        if (recommendation == null) {
            return null;
        }

        return new RecommendationResponse(
                toUserResponse(recommendation.getAssignee()),
                recommendation.getScore(),
                recommendation.getMatchedIssueCount(),
                new ArrayList<>(recommendation.getMatchedTerms()),
                new ArrayList<>(recommendation.getEvidenceIssueTitles()),
                recommendation.getCurrentOpenAssignedIssueCount(),
                recommendation.getExplanation()
        );
    }

    public static List<RecommendationResponse> toRecommendationResponses(
            List<AssigneeRecommendation> recommendations
    ) {
        List<RecommendationResponse> responses = new ArrayList<>();

        if (recommendations == null) {
            return responses;
        }

        for (AssigneeRecommendation recommendation : recommendations) {
            responses.add(toRecommendationResponse(recommendation));
        }

        return responses;
    }

    private static Map<String, Long> enumKeyMapToStringMap(Map<?, Long> source) {
        Map<String, Long> result = new LinkedHashMap<>();

        if (source == null) {
            return result;
        }

        for (Map.Entry<?, Long> entry : source.entrySet()) {
            Object key = entry.getKey();
            result.put(key == null ? "null" : key.toString(), entry.getValue());
        }

        return result;
    }

    private static Map<String, Long> localDateMapToStringMap(Map<LocalDate, Long> source) {
        Map<String, Long> result = new LinkedHashMap<>();

        if (source == null) {
            return result;
        }

        for (Map.Entry<LocalDate, Long> entry : source.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue());
        }

        return result;
    }

    private static Map<String, Long> yearMonthMapToStringMap(Map<YearMonth, Long> source) {
        Map<String, Long> result = new LinkedHashMap<>();

        if (source == null) {
            return result;
        }

        for (Map.Entry<YearMonth, Long> entry : source.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue());
        }

        return result;
    }

    private static Map<String, Long> stringKeyMapToStringMap(Map<String, Long> source) {
        Map<String, Long> result = new LinkedHashMap<>();

        if (source == null) {
            return result;
        }

        result.putAll(source);
        return result;
    }
}