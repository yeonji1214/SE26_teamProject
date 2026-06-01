package its.api.handler;

import com.sun.net.httpserver.HttpExchange;
import its.api.dto.ApiDtoMapper;
import its.api.ApiHandlerSupport;
import its.api.dto.CreateIssueRequest;
import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.service.ApplicationServices;
import its.service.IssueSearchCriteria;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class IssueApiHandler extends ApiHandlerSupport {

    public IssueApiHandler(ApplicationServices services) {
        super(services);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        try {
            List<String> segments = pathSegments(exchange);

            if (!isIssuesPath(segments)) {
                sendNotFound(exchange, "issue route not found");
                return;
            }

            if ("GET".equals(method(exchange)) && segments.size() == 2) {
                handleGetIssues(exchange);
                return;
            }

            if ("GET".equals(method(exchange)) && segments.size() == 3) {
                handleGetIssue(exchange, segments.get(2));
                return;
            }

            if ("POST".equals(method(exchange)) && segments.size() == 2) {
                handleCreateIssue(exchange);
                return;
            }

            sendMethodNotAllowed(exchange);
        } catch (IllegalArgumentException e) {
            sendBadRequest(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(exchange, e);
        }
    }

    private boolean isIssuesPath(List<String> segments) {
        return segments.size() >= 2
                && "api".equals(segments.get(0))
                && "issues".equals(segments.get(1));
    }

    private void handleGetIssues(HttpExchange exchange) throws IOException {
        IssueSearchCriteria criteria = buildSearchCriteria(queryParams(exchange));
        List<Issue> issues = services.getIssueService().searchIssues(criteria);

        sendJson(exchange, 200, ApiDtoMapper.toIssueResponses(issues));
    }

    private void handleGetIssue(HttpExchange exchange, String idSegment) throws IOException {
        Long issueId = parseLong(idSegment, "issueId");
        Issue issue = services.getIssueService().getIssue(issueId);

        sendJson(exchange, 200, ApiDtoMapper.toIssueResponse(issue));
    }

    private void handleCreateIssue(HttpExchange exchange) throws IOException {
        CreateIssueRequest request = readRequest(exchange, CreateIssueRequest.class);

        Issue issue = services.getIssueService().createIssue(
                request.getProjectId(),
                request.getTitle(),
                request.getDescription(),
                request.getReporterId(),
                parsePriorityOrNull(request.getPriority())
        );

        sendJson(exchange, 201, ApiDtoMapper.toIssueResponse(issue));
    }

    private IssueSearchCriteria buildSearchCriteria(Map<String, String> params) {
        IssueSearchCriteria criteria = new IssueSearchCriteria();

        if (params.containsKey("projectId")) {
            criteria.setProjectId(parseLong(params.get("projectId"), "projectId"));
        }

        if (params.containsKey("reporterId")) {
            criteria.setReporterId(parseLong(params.get("reporterId"), "reporterId"));
        }

        if (params.containsKey("assigneeId")) {
            criteria.setAssigneeId(parseLong(params.get("assigneeId"), "assigneeId"));
        }

        if (params.containsKey("status") && !params.get("status").isBlank()) {
            criteria.setStatus(IssueStatus.valueOf(params.get("status")));
        }

        if (params.containsKey("priority") && !params.get("priority").isBlank()) {
            criteria.setPriority(Priority.valueOf(params.get("priority")));
        }

        if (params.containsKey("keyword")) {
            criteria.setKeyword(params.get("keyword"));
        }

        return criteria;
    }

    private Priority parsePriorityOrNull(String priority) {
        if (priority == null || priority.isBlank()) {
            return null;
        }

        return Priority.valueOf(priority);
    }
}