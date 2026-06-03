package its.api.handler;

import com.sun.net.httpserver.HttpExchange;
import its.api.dto.ApiDtoMapper;
import its.api.ApiHandlerSupport;
import its.api.dto.CreateUserRequest;
import its.api.dto.LoginRequest;
import its.api.dto.PasswordLoginRequest;
import its.api.dto.UserResponse;
import its.domain.user.Role;
import its.domain.user.User;
import its.service.ApplicationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserApiHandler extends ApiHandlerSupport {

    public UserApiHandler(ApplicationServices services) {
        super(services);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        try {
            List<String> segments = pathSegments(exchange);

            if (isPasswordLoginPath(segments)) {
                handlePasswordLogin(exchange);
                return;
            }
            
            if (isLoginPath(segments)) {
                handleLogin(exchange);
                return;
            }

            if (!isUsersPath(segments)) {
                sendNotFound(exchange, "user route not found");
                return;
            }

            if ("GET".equals(method(exchange)) && segments.size() == 2) {
                handleGetUsers(exchange);
                return;
            }

            if ("GET".equals(method(exchange)) && segments.size() == 3) {
                handleGetUser(exchange, segments.get(2));
                return;
            }

            if ("POST".equals(method(exchange)) && segments.size() == 2) {
                handleCreateUser(exchange);
                return;
            }

            sendMethodNotAllowed(exchange);
        } catch (IllegalArgumentException e) {
            sendBadRequest(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(exchange, e);
        }
    }

    private boolean isPasswordLoginPath(List<String> segments) {
        return segments.size() == 3
                && "api".equals(segments.get(0))
                && "login".equals(segments.get(1))
                && "password".equals(segments.get(2));
    }

    private boolean isLoginPath(List<String> segments) {
        return segments.size() == 2
                && "api".equals(segments.get(0))
                && "login".equals(segments.get(1));
    }

    private boolean isUsersPath(List<String> segments) {
        return segments.size() >= 2
                && "api".equals(segments.get(0))
                && "users".equals(segments.get(1));
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(method(exchange))) {
            sendMethodNotAllowed(exchange);
            return;
        }

        LoginRequest request = readRequest(exchange, LoginRequest.class);
        User user = services.getUserService().getUser(request.getUserId());

        sendJson(exchange, 200, ApiDtoMapper.toUserResponse(user));
    }

    private void handlePasswordLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(method(exchange))) {
            sendMethodNotAllowed(exchange);
            return;
        }

        PasswordLoginRequest request = readRequest(exchange, PasswordLoginRequest.class);
        User user = services.getUserService().login(
                request.getUsername(),
                request.getPassword()
        );

        sendJson(exchange, 200, ApiDtoMapper.toUserResponse(user));
    }

    private void handleGetUsers(HttpExchange exchange) throws IOException {
        List<UserResponse> responses = new ArrayList<>();

        for (Object item : services.getUserService().getAllUsers()) {
            if (item instanceof User user) {
                responses.add(ApiDtoMapper.toUserResponse(user));
            }
        }

        sendJson(exchange, 200, responses);
    }

    private void handleGetUser(HttpExchange exchange, String idSegment) throws IOException {
        Long userId = parseLong(idSegment, "userId");
        User user = services.getUserService().getUser(userId);

        sendJson(exchange, 200, ApiDtoMapper.toUserResponse(user));
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        CreateUserRequest request = readRequest(exchange, CreateUserRequest.class);

        User user = services.getUserService().createUser(
                request.getUsername(),
                request.getPassword(),
                Role.valueOf(request.getRole())
        );

        sendJson(exchange, 201, ApiDtoMapper.toUserResponse(user));
    }
}