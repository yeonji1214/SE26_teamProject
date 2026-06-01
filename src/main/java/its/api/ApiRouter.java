package its.api;

import com.sun.net.httpserver.HttpExchange;
import its.api.handler.ProjectApiHandler;
import its.api.handler.UserApiHandler;
import its.service.ApplicationServices;

import java.io.IOException;
import java.util.List;

public class ApiRouter extends ApiHandlerSupport {
    private final UserApiHandler userApiHandler;
    private final ProjectApiHandler projectApiHandler;

    public ApiRouter(ApplicationServices services) {
        super(services);
        this.userApiHandler = new UserApiHandler(services);
        this.projectApiHandler = new ProjectApiHandler(services);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        List<String> segments = pathSegments(exchange);

        if (segments.size() >= 2 && "users".equals(segments.get(1))) {
            userApiHandler.handle(exchange);
            return;
        }

        if (segments.size() >= 2 && "login".equals(segments.get(1))) {
            userApiHandler.handle(exchange);
            return;
        }

        if (segments.size() >= 2 && "projects".equals(segments.get(1))) {
            projectApiHandler.handle(exchange);
            return;
        }

        sendNotFound(exchange, "API route not found: " + exchange.getRequestURI().getPath());
    }
}