package doczilla.com.task2.fileexchange.adapters.in.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import doczilla.com.task2.fileexchange.application.ports.in.DownloadFileUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.UploadFileUseCase;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileController implements HttpHandler {

    private final UploadFileUseCase uploadUseCase;
    private final DownloadFileUseCase downloadUseCase;
    private final AuthExtractor authExtractor;
    private final MultipartParser multipartParser;

    public FileController(
            UploadFileUseCase uploadUseCase,
            DownloadFileUseCase downloadUseCase
    ) {
        this.uploadUseCase = uploadUseCase;
        this.downloadUseCase = downloadUseCase;
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
            } else {
                sendError(exchange, 404, "Not found");
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleUpload(HttpExchange exchange) throws IOException {
        // Извлекаем пользователя (может быть null)
        UserId user = authExtractor.extract(exchange).orElse(null);

        // Парсим multipart
        ParsedFile parsed = multipartParser.parse(exchange.getRequestBody());

        // Формируем команду
        UploadFileUseCase.UploadFileCommand command =
                new UploadFileUseCase.UploadFileCommand(
                        parsed.fileName(),
                        parsed.contentType(),
                        parsed.content(),
                        parsed.content().length,
                        user
                );

        // Вызываем use case
        FileId fileId = uploadUseCase.upload(command);

        // Формируем ответ
        String json = String.format(
                "{\"downloadUrl\":\"/download/%s\",\"expiresInDays\":30}",
                fileId.value()
        );
        sendJson(exchange, 200, json);
    }

    private void handleDownload(HttpExchange exchange, String path) throws IOException {
        String fileIdStr = path.substring(path.lastIndexOf('/') + 1);
        FileId fileId = FileId.of(fileIdStr);
        UserId user = authExtractor.extract(exchange).orElse(null);

        DownloadFileUseCase.FileResult result = downloadUseCase
                .download(fileId, user)
                .orElseThrow(() -> new FileNotFoundException(fileIdStr));

        exchange.getResponseHeaders().set("Content-Type", result.contentType());
        exchange.getResponseHeaders().set("Content-Disposition",
                "attachment; filename=\"" + result.fileName() + "\"");
        exchange.sendResponseHeaders(200, result.content().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(result.content());
        }
    }

    private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private void sendError(HttpExchange exchange, int code, String msg) throws IOException {
        sendJson(exchange, code, "{\"error\":\"" + msg + "\"}");
    }

    private void handleException(HttpExchange exchange, Exception e) throws IOException {
        int code = switch (e) {
            case IllegalArgumentException iae -> 400;
            case SecurityException se -> 401;
            case FileNotFoundException fnf -> 404;
            default -> 500;
        };
        sendError(exchange, code, e.getMessage());
        exchange.close();
    }
}
