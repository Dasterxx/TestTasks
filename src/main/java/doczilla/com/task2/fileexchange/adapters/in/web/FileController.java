package doczilla.com.task2.fileexchange.adapters.in.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import doczilla.com.task2.fileexchange.application.ports.in.CleanupExpiredFilesUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.DownloadFileUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.GetStatisticsUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.LoginUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.UploadFileUseCase;
import doczilla.com.task2.fileexchange.domain.exceptions.FileNotFoundEx;
import doczilla.com.task2.fileexchange.domain.exceptions.UnauthorizedAccessException;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FileController implements HttpHandler {

    private final UploadFileUseCase uploadUseCase;
    private final DownloadFileUseCase downloadUseCase;
    private final GetStatisticsUseCase statisticsUseCase;
    private final LoginUseCase loginUseCase;
    private final AuthExtractor authExtractor;
    private final MultipartParser multipartParser;
    private final CleanupExpiredFilesUseCase cleanupUseCase;

    public FileController(
            UploadFileUseCase uploadUseCase,
            DownloadFileUseCase downloadUseCase,
            GetStatisticsUseCase statisticsUseCase,
            LoginUseCase loginUseCase, CleanupExpiredFilesUseCase cleanupUseCase
    ) {
        this.uploadUseCase = uploadUseCase;
        this.downloadUseCase = downloadUseCase;
        this.statisticsUseCase = statisticsUseCase;
        this.loginUseCase = loginUseCase;
        this.cleanupUseCase = cleanupUseCase;
        this.authExtractor = new AuthExtractor();
        this.multipartParser = new MultipartParser();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            System.out.println("Request: " + method + " " + path);  // Для отладки

            // Проверяем маршруты правильно!
            if ("POST".equals(method) && "/upload".equals(path)) {
                handleUpload(exchange);
            } else if ("GET".equals(method) && path.startsWith("/download/")) {
                handleDownload(exchange, path);
            } else if ("GET".equals(method) && "/stats".equals(path)) {
                handleStats(exchange);
            } else if ("POST".equals(method) && "/login".equals(path)) {
                handleLogin(exchange);
            } else if ("GET".equals(method) && "/me".equals(path)) {
                handleMe(exchange);
            }else if ("POST".equals(method) && "/admin/cleanup".equals(path)) {
                handleCleanup(exchange);
            } else {
                System.out.println("No route found for: " + method + " " + path);
                sendError(exchange, 404, "Not found");
            }
        } catch (UnauthorizedAccessException e) {
            sendError(exchange, 403, "Access denied");
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleUpload(HttpExchange exchange) throws IOException {
        UserId user = authExtractor.extract(exchange).orElse(null);

        // Если хотим ОБЯЗАТЕЛЬНУЮ авторизацию:
        // if (user == null) {
        //     sendError(exchange, 401, "Authorization required. Please login.");
        //     return;
        // }

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        ParsedFile parsed = multipartParser.parse(exchange.getRequestBody(), contentType);

        UploadFileUseCase.UploadFileCommand command = new UploadFileUseCase.UploadFileCommand(
                parsed.fileName(),
                parsed.contentType(),
                parsed.content(),
                parsed.content().length,
                user
        );

        FileId fileId = uploadUseCase.upload(command);

        String json = String.format(
                "{\"downloadUrl\":\"/download/%s\",\"expiresInDays\":30,\"fileId\":\"%s\",\"isPublic\":%s}",
                fileId.value(),
                fileId.value(),
                user == null
        );
        sendJson(exchange, 200, json);
    }

    private void handleDownload(HttpExchange exchange, String path) throws IOException {
        String fileIdStr = path.substring("/download/".length());
        FileId fileId = FileId.of(fileIdStr);

        UserId user = authExtractor.extract(exchange).orElse(null);

        DownloadFileUseCase.FileResult result = downloadUseCase
                .download(fileId, user)
                .orElseThrow(() -> new FileNotFoundEx(fileIdStr));

        exchange.getResponseHeaders().set("Content-Type", result.contentType());
        exchange.getResponseHeaders().set("Content-Disposition",
                "attachment; filename=\"" + result.fileName() + "\"");
        exchange.sendResponseHeaders(200, result.content().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(result.content());
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        // Читаем JSON body
        String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));

        // Простой парсинг JSON
        String username = extractJsonValue(body, "username");
        String password = extractJsonValue(body, "password");

        if (username == null || username.isBlank()) {
            sendError(exchange, 400, "Username required");
            return;
        }

        LoginUseCase.LoginResult result = loginUseCase.login(username, password);

        String json = String.format(
                "{\"token\":\"%s\",\"userId\":\"%s\",\"username\":\"%s\"}",
                result.token(), result.userId(), escapeJson(username)
        );
        sendJson(exchange, 200, json);
    }

    private void handleMe(HttpExchange exchange) throws IOException {
        UserId user = authExtractor.extract(exchange).orElse(null);

        if (user == null) {
            sendError(exchange, 401, "Not authenticated");
            return;
        }

        String json = String.format("{\"userId\":\"%s\"}", user.toString());
        sendJson(exchange, 200, json);
    }

    private void handleStats(HttpExchange exchange) throws IOException {
        GetStatisticsUseCase.Statistics stats = statisticsUseCase.getStatistics();

        String json = String.format(
                "{\"totalFiles\":%d,\"totalDownloads\":%d,\"totalBytesUploaded\":%d}",
                stats.totalFiles(), stats.totalDownloads(), stats.totalBytesUploaded()
        );

        sendJson(exchange, 200, json);
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private String escapeJson(String s) {
        return s.replace("\"", "\\\"");
    }

    private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int code, String msg) throws IOException {
        sendJson(exchange, code, "{\"error\":\"" + msg + "\"}");
    }

    private void handleException(HttpExchange exchange, Exception e) throws IOException {
        System.out.println("Exception: " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();

        int code = switch (e) {
            case IllegalArgumentException iae -> 400;
            case SecurityException se -> 401;
            case FileNotFoundEx fnf -> 404;
            default -> 500;
        };
        sendError(exchange, code, e.getMessage());
    }

    private void handleCleanup(HttpExchange exchange) throws IOException {
        // Можно добавить проверку админа, но для простоты - открыто
        CleanupExpiredFilesUseCase.CleanupResult result = cleanupUseCase.cleanup();

        String json = String.format(
                "{\"deletedCount\":%d,\"freedBytes\":%d}",
                result.deletedCount(),
                result.freedBytes()
        );
        sendJson(exchange, 200, json);
    }
}