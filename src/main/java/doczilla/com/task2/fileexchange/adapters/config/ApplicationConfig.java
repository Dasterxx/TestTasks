package doczilla.com.task2.fileexchange.adapters.config;

import com.sun.net.httpserver.HttpServer;
import doczilla.com.task2.fileexchange.adapters.in.web.FileController;
import doczilla.com.task2.fileexchange.adapters.out.persistence.InMemoryFileIndexAdapter;
import doczilla.com.task2.fileexchange.adapters.out.persistence.LocalFileStorageAdapter;
import doczilla.com.task2.fileexchange.application.ports.in.DownloadFileUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.UploadFileUseCase;
import doczilla.com.task2.fileexchange.application.ports.out.AuditLogPort;
import doczilla.com.task2.fileexchange.application.ports.out.FileNotifierPort;
import doczilla.com.task2.fileexchange.application.service.DownloadFileService;
import doczilla.com.task2.fileexchange.application.service.UploadFileService;
import doczilla.com.task2.fileexchange.domain.model.File;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class ApplicationConfig {

    public HttpServer createServer(int port) throws IOException {
        // 1. Outgoing Adapters (Driven)
        FileStoragePort storage = new LocalFileStorageAdapter("uploads");
        FileIndexPort index = new InMemoryFileIndexAdapter();
        AuditLogPort auditLog = new ConsoleAuditLogAdapter();
        FileNotifierPort notifier = new ConsoleNotifierAdapter();

        // 2. Application Services (Use Cases)
        UploadFileUseCase uploadUseCase = new UploadFileService(
                storage, index, notifier, auditLog
        );
        DownloadFileUseCase downloadUseCase = new DownloadFileService(
                index, storage
        );

        // 3. Incoming Adapters (Driving)
        FileController controller = new FileController(
                uploadUseCase, downloadUseCase
        );

        // 4. HTTP Server
        HttpServer server = HttpServer.create(
                new InetSocketAddress(port), 0
        );
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/", controller);

        return server;
    }

    // Вспомогательные адаптеры
    private static class ConsoleAuditLogAdapter implements AuditLogPort {
        @Override
        public void log(String action, FileId targetId, UserId userId) {
            System.out.printf("[AUDIT] %s: %s by %s%n", action, targetId, userId);
        }
    }

    private static class ConsoleNotifierAdapter implements FileNotifierPort {
        @Override
        public void notifyFileUploaded(File file) {
            System.out.println("File uploaded: " + file.getName());
        }
    }
}
