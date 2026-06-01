package its.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import its.api.dto.ApiErrorResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonUtil() {
    }

    public static <T> T readJson(HttpExchange exchange, Class<T> type) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return OBJECT_MAPPER.readValue(inputStream, type);
        }
    }

    public static String toJson(Object value) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(value);
    }

    public static void writeJson(HttpExchange exchange, int statusCode, Object value) throws IOException {
        addDefaultHeaders(exchange);

        byte[] response = toJson(value).getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    public static void writeNoContent(HttpExchange exchange, int statusCode) throws IOException {
        addDefaultHeaders(exchange);
        exchange.sendResponseHeaders(statusCode, -1);
        exchange.close();
    }

    public static void writeError(
            HttpExchange exchange,
            int statusCode,
            String error,
            String message
    ) throws IOException {
        writeJson(
                exchange,
                statusCode,
                new ApiErrorResponse(
                        error,
                        message,
                        statusCode,
                        exchange.getRequestURI().getPath()
                )
        );
    }

    private static void addDefaultHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PATCH, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
}