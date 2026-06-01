package its.api;

import com.sun.net.httpserver.HttpExchange;
import its.service.ApplicationServices;

import java.io.IOException;

public class ApiRouter extends ApiHandlerSupport {

    public ApiRouter(ApplicationServices services) {
        super(services);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        sendNotFound(exchange, "API route not found: " + exchange.getRequestURI().getPath());
    }
}