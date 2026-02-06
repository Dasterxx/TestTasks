package doczilla.com.task2.fileexchange.adapters.config;

import doczilla.com.task2.fileexchange.adapters.in.web.FileController;
import doczilla.com.task2.fileexchange.adapters.out.audit.ConsoleAuditLogAdapter;
import doczilla.com.task2.fileexchange.adapters.out.notification.ConsoleNotifierAdapter;
import doczilla.com.task2.fileexchange.adapters.out.persistence.InMemoryFileIndexAdapter;
import doczilla.com.task2.fileexchange.adapters.out.persistence.InMemoryUserRepository;
import doczilla.com.task2.fileexchange.adapters.out.persistence.LocalFileStorageAdapter;
import doczilla.com.task2.fileexchange.application.ports.in.LoginUseCase;
import doczilla.com.task2.fileexchange.application.ports.out.AuditLogPort;
import doczilla.com.task2.fileexchange.application.ports.out.FileNotifierPort;
import doczilla.com.task2.fileexchange.application.service.DownloadFileService;
import doczilla.com.task2.fileexchange.application.service.LoginService;
import doczilla.com.task2.fileexchange.application.service.StatisticsService;
import doczilla.com.task2.fileexchange.application.service.UploadFileService;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;
import doczilla.com.task2.fileexchange.domain.repository.UserRepository;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class ApplicationConfig {

    public Application compose() throws IOException {
        // Repositories
        FileStoragePort storage = new LocalFileStorageAdapter("uploads");
        FileIndexPort index = new InMemoryFileIndexAdapter();
        UserRepository userRepo = new InMemoryUserRepository();

        // Services
        AuditLogPort auditLog = new ConsoleAuditLogAdapter();
        FileNotifierPort notifier = new ConsoleNotifierAdapter();
        StatisticsService statsService = new StatisticsService(index);

        // Use Cases
        UploadFileService upload = new UploadFileService(storage, index, notifier, auditLog);
        DownloadFileService download = new DownloadFileService(index, storage);
        LoginUseCase login = new LoginService(userRepo);

        // Controller
        FileController controller = new FileController(upload, download, statsService, login);

        // HTTP Server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/", controller);

        return new Application(server);
    }

    public record Application(HttpServer server) {
        public void start() {
            server.start();
            System.out.println("=================================");
            System.out.println("🚀 Backend: http://localhost:8080");
            System.out.println("=================================");
        }
    }
}