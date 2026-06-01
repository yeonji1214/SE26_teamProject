package its.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import its.service.ApplicationServices;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ApiHandlerSupport implements HttpHandler {
    protected final ApplicationServices services;

    protected ApiHandlerSupport(ApplicationServices services) {
        if (services == null) {
            throw new IllegalArgumentException("services must not be null");
        }

        this.services = services;
    }

    protected boolean handleOptions(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            JsonUtil.writeNoContent(exchange, 204);
            return true;
        }

        return false;
    }

    protected String method(HttpExchange exchange) {
        return exchange.getRequestMethod().toUpperCase();
    }

    protected List<String> pathSegments(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] rawSegments = path.split("/");

        List<String> segments = new ArrayList<>();

        for (String segment : rawSegments) {
            if (!segment.isBlank()) {
                segments.add(segment);
            }
        }

        return segments;
    }

    protected Map<String, String> queryParams(HttpExchange exchange) {
        Map<String, String> params = new LinkedHashMap<>();
        String rawQuery = exchange.getRequestURI().getRawQuery();

        if (rawQuery == null || rawQuery.isBlank()) {
            return params;
        }

        String[] pairs = rawQuery.split("&");

        for (String pair : pairs) {
            if (pair.isBlank()) {
                continue;
            }

            String[] keyValue = pair.split("=", 2);
            String key = decode(keyValue[0]);
            String value = keyValue.length > 1 ? decode(keyValue[1]) : "";

            params.put(key, value);
        }

        return params;
    }

    protected Long parseLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a number: " + value);
        }
    }

    protected Integer parseIntegerOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    protected <T> T readRequest(HttpExchange exchange, Class<T> type) throws IOException {
        return JsonUtil.readJson(exchange, type);
    }

    protected void sendJson(HttpExchange exchange, int statusCode, Object value) throws IOException {
        JsonUtil.writeJson(exchange, statusCode, value);
    }

    protected void sendNoContent(HttpExchange exchange, int statusCode) throws IOException {
        JsonUtil.writeNoContent(exchange, statusCode);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        JsonUtil.writeError(exchange, 400, "Bad Request", message);
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        JsonUtil.writeError(exchange, 404, "Not Found", message);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        JsonUtil.writeError(exchange, 405, "Method Not Allowed", "method not allowed");
    }

    protected void sendInternalServerError(HttpExchange exchange, Exception exception) throws IOException {
        JsonUtil.writeError(exchange, 500, "Internal Server Error", exception.getMessage());
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}