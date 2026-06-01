package its.api;

import com.sun.net.httpserver.HttpServer;
import its.service.ApplicationServices;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ApiServer {
    private final HttpServer server;
    private final int port;

    private ApiServer(int port, ApplicationServices services) throws IOException {
        this.port = port;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.createContext("/api", new ApiRouter(services));
        this.server.setExecutor(Executors.newCachedThreadPool());
    }

    public static ApiServer create(int port, ApplicationServices services) {
        try {
            return new ApiServer(port, services);
        } catch (IOException e) {
            throw new IllegalStateException("failed to create API server", e);
        }
    }

    public void start() {
        server.start();
        System.out.println("HTTP API server started at http://localhost:" + port);
    }

    public void stop(int delaySeconds) {
        server.stop(delaySeconds);
    }
}