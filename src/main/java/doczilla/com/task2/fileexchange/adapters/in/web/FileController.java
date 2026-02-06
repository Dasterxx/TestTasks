package doczilla.com.task2.fileexchange.adapters.in.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import doczilla.com.task2.fileexchange.application.ports.in.DownloadFileUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.GetStatisticsUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.UploadFileUseCase;
import doczilla.com.task2.fileexchange.domain.exceptions.FileNotFoundEx;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileController implements HttpHandler {

    private final UploadFileUseCase uploadUseCase;
    private final DownloadFileUseCase downloadUseCase;
    private final GetStatisticsUseCase statisticsUseCase;  // Добавляем
    private final AuthExtractor authExtractor;
    private final MultipartParser multipartParser;

    public FileController(
            UploadFileUseCase uploadUseCase,
            DownloadFileUseCase downloadUseCase,
            GetStatisticsUseCase statisticsUseCase  // Добавляем
    ) {
        this.uploadUseCase = uploadUseCase;
        this.downloadUseCase = downloadUseCase;
        this.statisticsUseCase = statisticsUseCase;  // Добавляем
        this.authExtractor = new AuthExtractor();
        this.multipartParser = new MultipartParser();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if ("POST".equals(method) && path.equals("/upload")) {
                handleUpload(exchange);
            } else if ("GET".equals(method) && path.startsWith("/download/")) {
                handleDownload(exchange, path);
            } else if ("GET".equals(method) && path.equals("/stats")) {  // Статистика
                handleStats(exchange);
            } else if ("POST".equals(method) && path.equals("/login")) {  // Логин (простой)
                handleLogin(exchange);
            } else {
                sendError(exchange, 404, "Not found");
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleUpload(HttpExchange exchange) throws IOException {
        // Проверяем авторизацию (опционально - можно убрать если разрешаем анонимов)
        UserId user = authExtractor.extract(exchange).orElse(null);

        // Если требуется авторизация:
        // if (user == null) {
        //     sendError(exchange, 401, "Authorization required");
        //     return;
        // }

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        ParsedFile parsed = multipartParser.parse(exchange.getRequestBody(), contentType);

        UploadFileUseCase.UploadFileCommand command =
                new UploadFileUseCase.UploadFileCommand(
                        parsed.fileName(),
                        parsed.contentType(),
                        parsed.content(),
                        parsed.content().length,
                        user  // null если аноним
                );

        FileId fileId = uploadUseCase.upload(command);

        String json = String.format(
                "{\"downloadUrl\":\"/download/%s\",\"expiresInDays\":30}",
                fileId.value()
        );
        sendJson(exchange, 200, json);
    }

    private void handleDownload(HttpExchange exchange, String path) throws IOException {
        String fileIdStr = path.substring(path.lastIndexOf('/') + 1);
        FileId fileId = FileId.of(fileIdStr);

        // Проверяем авторизацию (опционально)
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

    private void handleStats(HttpExchange exchange) throws IOException {
        GetStatisticsUseCase.Statistics stats = statisticsUseCase.getStatistics();

        String json = String.format(
                "{\"totalFiles\":%d,\"totalDownloads\":%d,\"totalBytesUploaded\":%d}",
                stats.totalFiles(),
                stats.totalDownloads(),
                stats.totalBytesUploaded()
        );

        sendJson(exchange, 200, json);
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        // Простая заглушка для логина - генерируем токен как UUID
        // В реальности здесь проверка логина/пароля
        String token = java.util.UUID.randomUUID().toString();
        String json = String.format("{\"token\":\"%s\",\"type\":\"Bearer\"}", token);
        sendJson(exchange, 200, json);
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
}