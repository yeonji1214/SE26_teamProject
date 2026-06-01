package its.api.handler;

import com.sun.net.httpserver.HttpExchange;
import its.api.dto.ApiDtoMapper;
import its.api.ApiHandlerSupport;
import its.api.dto.CreateProjectRequest;
import its.api.dto.ProjectResponse;
import its.domain.project.Project;
import its.service.ApplicationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectApiHandler extends ApiHandlerSupport {

    public ProjectApiHandler(ApplicationServices services) {
        super(services);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        try {
            List<String> segments = pathSegments(exchange);

            if (!isProjectsPath(segments)) {
                sendNotFound(exchange, "project route not found");
                return;
            }

            if ("GET".equals(method(exchange)) && segments.size() == 2) {
                handleGetProjects(exchange);
                return;
            }

            if ("GET".equals(method(exchange)) && segments.size() == 3) {
                handleGetProject(exchange, segments.get(2));
                return;
            }

            if ("POST".equals(method(exchange)) && segments.size() == 2) {
                handleCreateProject(exchange);
                return;
            }

            sendMethodNotAllowed(exchange);
        } catch (IllegalArgumentException e) {
            sendBadRequest(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(exchange, e);
        }
    }

    private boolean isProjectsPath(List<String> segments) {
        return segments.size() >= 2
                && "api".equals(segments.get(0))
                && "projects".equals(segments.get(1));
    }

    private void handleGetProjects(HttpExchange exchange) throws IOException {
        List<ProjectResponse> responses = new ArrayList<>();

        for (Object item : services.getProjectService().getAllProjects()) {
            if (item instanceof Project project) {
                responses.add(ApiDtoMapper.toProjectResponse(project));
            }
        }

        sendJson(exchange, 200, responses);
    }

    private void handleGetProject(HttpExchange exchange, String idSegment) throws IOException {
        Long projectId = parseLong(idSegment, "projectId");
        Project project = services.getProjectService().getProject(projectId);

        sendJson(exchange, 200, ApiDtoMapper.toProjectResponse(project));
    }

    private void handleCreateProject(HttpExchange exchange) throws IOException {
        CreateProjectRequest request = readRequest(exchange, CreateProjectRequest.class);

        Project project = services.getProjectService().createProject(
                request.getName(),
                request.getDescription()
        );

        sendJson(exchange, 201, ApiDtoMapper.toProjectResponse(project));
    }
}