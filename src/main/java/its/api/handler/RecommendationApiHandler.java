package its.api.handler;

import com.sun.net.httpserver.HttpExchange;
import its.api.dto.ApiDtoMapper;
import its.api.ApiHandlerSupport;
import its.service.ApplicationServices;
import its.service.AssigneeRecommendation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RecommendationApiHandler extends ApiHandlerSupport {

    public RecommendationApiHandler(ApplicationServices services) {
        super(services);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        try {
            List<String> segments = pathSegments(exchange);

            if (!isRecommendationPath(segments)) {
                sendNotFound(exchange, "recommendation route not found");
                return;
            }

            if ("GET".equals(method(exchange))) {
                handleGetRecommendations(exchange, segments.get(2));
                return;
            }

            sendMethodNotAllowed(exchange);
        } catch (IllegalArgumentException e) {
            sendBadRequest(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(exchange, e);
        }
    }

    public boolean isRecommendationPath(List<String> segments) {
        return segments.size() == 4
                && "api".equals(segments.get(0))
                && "issues".equals(segments.get(1))
                && "recommendations".equals(segments.get(3));
    }

    private void handleGetRecommendations(HttpExchange exchange, String idSegment) throws IOException {
        Long issueId = parseLong(idSegment, "issueId");
        Map<String, String> queryParams = queryParams(exchange);
        int limit = parseIntegerOrDefault(queryParams.get("limit"), 3);

        List<AssigneeRecommendation> recommendations =
                services.getRecommendationService().recommendAssignees(issueId, limit);

        sendJson(exchange, 200, ApiDtoMapper.toRecommendationResponses(recommendations));
    }
}