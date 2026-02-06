// adapters/config/ApplicationConfig.java
package doczilla.com.task2.fileexchange.adapters.config;

import doczilla.com.task2.fileexchange.adapters.in.web.FileController;
import doczilla.com.task2.fileexchange.adapters.out.audit.ConsoleAuditLogAdapter;
import doczilla.com.task2.fileexchange.adapters.out.notification.ConsoleNotifierAdapter;
import doczilla.com.task2.fileexchange.adapters.out.persistence.InMemoryFileIndexAdapter;
import doczilla.com.task2.fileexchange.adapters.out.persistence.LocalFileStorageAdapter;
import doczilla.com.task2.fileexchange.application.ports.in.GetStatisticsUseCase;
import doczilla.com.task2.fileexchange.application.ports.out.AuditLogPort;
import doczilla.com.task2.fileexchange.application.ports.out.FileNotifierPort;
import doczilla.com.task2.fileexchange.application.service.DownloadFileService;
import doczilla.com.task2.fileexchange.application.service.StatisticsService;
import doczilla.com.task2.fileexchange.application.service.UploadFileService;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class ApplicationConfig {

    public Application compose() throws IOException {
        // Repositories (Adapters)
        FileStoragePort storage = new LocalFileStorageAdapter("uploads");
        FileIndexPort index = new InMemoryFileIndexAdapter();

        // Outgoing Ports
        AuditLogPort auditLog = new ConsoleAuditLogAdapter();
        FileNotifierPort notifier = new ConsoleNotifierAdapter();

        // Statistics (shared between services)
        StatisticsService statsService = new StatisticsService(index);

        // Use Cases
        UploadFileService upload = new UploadFileService(storage, index, notifier, auditLog);
        DownloadFileService download = new DownloadFileService(index, storage);

        // Controller
        FileController controller = new FileController(upload, download, statsService);

        // HTTP Server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/", controller);

        return new Application(server);
    }

    public record Application(HttpServer server) {
        public void start() {
            server.start();
            System.out.println("Server started on port 8080");
        }
    }
}