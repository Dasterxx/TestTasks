package doczilla.com.task2.fileexchange.adapters.scheduler;

import doczilla.com.task2.fileexchange.adapters.config.AppConfig;
import doczilla.com.task2.fileexchange.application.ports.in.CleanupExpiredFilesUseCase;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CleanupScheduler {

    private final ScheduledExecutorService scheduler;
    private final CleanupExpiredFilesUseCase cleanupUseCase;

    public CleanupScheduler(CleanupExpiredFilesUseCase cleanupUseCase) {
        this.cleanupUseCase = cleanupUseCase;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("cleanup-scheduler");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        long interval = AppConfig.CLEANUP_INTERVAL_HOURS;
        System.out.println("Starting cleanup scheduler (interval: " + interval + " hours)");

        // Первый запуск через 1 минуту, потом по расписанию
//        scheduler.scheduleAtFixedRate(
//                this::runCleanup,
//                1,
//                interval,
//                TimeUnit.HOURS
//        );

        // test delete
        scheduler.scheduleAtFixedRate(
                this::runCleanup,
                1,
                1,
                TimeUnit.MINUTES
        );
    }

    private void runCleanup() {
        try {
            System.out.println("[" + java.time.LocalDateTime.now() + "] Running scheduled cleanup...");
            CleanupExpiredFilesUseCase.CleanupResult result = cleanupUseCase.cleanup();
            System.out.println("Cleanup completed: deleted " + result.deletedCount() +
                    " files, freed " + formatBytes(result.freedBytes()));
        } catch (Exception e) {
            System.err.println("Cleanup job failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        scheduler.shutdown();
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}