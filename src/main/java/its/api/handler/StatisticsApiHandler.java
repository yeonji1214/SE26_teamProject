package its.api.handler;

import com.sun.net.httpserver.HttpExchange;
import its.api.dto.ApiDtoMapper;
import its.api.ApiHandlerSupport;
import its.service.ApplicationServices;

import java.io.IOException;
import java.util.List;

public class StatisticsApiHandler extends ApiHandlerSupport {

    public StatisticsApiHandler(ApplicationServices services) {
        super(services);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        try {
            List<String> segments = pathSegments(exchange);

            if (!isStatisticsPath(segments)) {
                sendNotFound(exchange, "statistics route not found");
                return;
            }

            if ("GET".equals(method(exchange)) && segments.size() == 2) {
                sendJson(exchange, 200, ApiDtoMapper.toStatisticsResponse(services.getStatisticsService()));
                return;
            }

            sendMethodNotAllowed(exchange);
        } catch (IllegalArgumentException e) {
            sendBadRequest(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(exchange, e);
        }
    }

    private boolean isStatisticsPath(List<String> segments) {
        return segments.size() >= 2
                && "api".equals(segments.get(0))
                && "statistics".equals(segments.get(1));
    }
}