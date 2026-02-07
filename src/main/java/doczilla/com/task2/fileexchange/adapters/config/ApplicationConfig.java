// adapters/config/ApplicationConfig.java
package doczilla.com.task2.fileexchange.adapters.config;

import doczilla.com.task2.fileexchange.adapters.in.web.FileController;
import doczilla.com.task2.fileexchange.adapters.out.audit.ConsoleAuditLogAdapter;
import doczilla.com.task2.fileexchange.adapters.out.notification.ConsoleNotifierAdapter;
import doczilla.com.task2.fileexchange.adapters.out.persistence.InMemoryFileIndexAdapter;
import doczilla.com.task2.fileexchange.adapters.out.persistence.InMemoryUserRepository;
import doczilla.com.task2.fileexchange.adapters.out.persistence.LocalFileStorageAdapter;
import doczilla.com.task2.fileexchange.adapters.scheduler.CleanupScheduler;
import doczilla.com.task2.fileexchange.application.ports.in.CleanupExpiredFilesUseCase;
import doczilla.com.task2.fileexchange.application.ports.in.LoginUseCase;
import doczilla.com.task2.fileexchange.application.ports.out.AuditLogPort;
import doczilla.com.task2.fileexchange.application.ports.out.FileNotifierPort;
import doczilla.com.task2.fileexchange.application.service.CleanupService;
import doczilla.com.task2.fileexchange.application.service.DownloadFileService;
import doczilla.com.task2.fileexchange.application.service.LoginService;
import doczilla.com.task2.fileexchange.application.service.StatisticsService;
import doczilla.com.task2.fileexchange.application.service.UploadFileService;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;
import doczilla.com.task2.fileexchange.domain.repository.UserRepository;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class ApplicationConfig {

    public Application compose() throws Exception {
        // Печатаем конфигурацию
        AppConfig.printConfig();

        // Repositories - теперь без параметров, пути из AppConfig!
        FileStoragePort storage = new LocalFileStorageAdapter();
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
        CleanupExpiredFilesUseCase cleanup = new CleanupService(index, storage, notifier);

        // Controller
        FileController controller = new FileController(upload, download, statsService, login, cleanup);

        // HTTP Server
        HttpServer server = HttpServer.create(
                new InetSocketAddress(AppConfig.SERVER_HOST, AppConfig.SERVER_PORT),
                0
        );
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/", controller);

        // Cleanup Scheduler
        CleanupScheduler scheduler = null;
        if (AppConfig.CLEANUP_ENABLED) {
            scheduler = new CleanupScheduler(cleanup);
        }

        return new Application(server, scheduler, (InMemoryFileIndexAdapter) index);
    }

    public record Application(
            HttpServer server,
            CleanupScheduler scheduler,
            InMemoryFileIndexAdapter indexAdapter
    ) {
        public void start() {
            server.start();

            if (scheduler != null) {
                scheduler.start();
            }

            // Hook для graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down...");
                if (indexAdapter != null) {
                    indexAdapter.shutdown();
                }
                if (scheduler != null) {
                    scheduler.stop();
                }
                server.stop(0);
                System.out.println("Goodbye!");
            }));

            System.out.println("\n🚀 Server ready at http://" +
                    AppConfig.SERVER_HOST + ":" + AppConfig.SERVER_PORT);
        }
    }
}