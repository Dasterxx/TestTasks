package doczilla.com.task2.fileexchange.adapters.in.web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;


public class StaticFileServer {

    private final Path frontendDir;
    private final int port;

    public StaticFileServer(String frontendPath, int port) {
        this.frontendDir = Path.of(frontendPath).toAbsolutePath().normalize();
        this.port = port;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", this::handleRequest);

        server.start();
        System.out.println("Frontend server started on http://localhost:" + port);
        System.out.println("Serving files from: " + frontendDir);
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";

        // Безопасность: не даём выйти за пределы frontendDir
        Path filePath = frontendDir.resolve(path.substring(1)).normalize();
        if (!filePath.startsWith(frontendDir)) {
            send404(exchange);
            return;
        }

        if (!Files.exists(filePath)) {
            send404(exchange);
            return;
        }

        String contentType = guessContentType(filePath);
        byte[] content = Files.readAllBytes(filePath);

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private String guessContentType(Path path) {
        String name = path.toString().toLowerCase();
        if (name.endsWith(".html")) return "text/html";
        if (name.endsWith(".css")) return "text/css";
        if (name.endsWith(".js")) return "application/javascript";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }

    private void send404(HttpExchange exchange) throws IOException {
        String msg = "Not found";
        exchange.sendResponseHeaders(404, msg.length());
        exchange.getResponseBody().write(msg.getBytes());
    }
}
