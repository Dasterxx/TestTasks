package doczilla.com.task2.fileexchange.application.service;

import doczilla.com.task2.fileexchange.application.ports.in.CleanupExpiredFilesUseCase;
import doczilla.com.task2.fileexchange.application.ports.out.FileNotifierPort;
import doczilla.com.task2.fileexchange.domain.model.File;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class CleanupService implements CleanupExpiredFilesUseCase {

    private final FileIndexPort indexPort;
    private final FileStoragePort storagePort;
    private final FileNotifierPort notifierPort;
    private static final int EXPIRY_DAYS = 0;

    public CleanupService(
            FileIndexPort indexPort,
            FileStoragePort storagePort,
            FileNotifierPort notifierPort
    ) {
        this.indexPort = Objects.requireNonNull(indexPort);
        this.storagePort = Objects.requireNonNull(storagePort);
        this.notifierPort = Objects.requireNonNull(notifierPort);
    }

    @Override
    public CleanupResult cleanup() {
        System.out.println("=== Starting cleanup job ===");

        Instant cutoff = Instant.now().minus(EXPIRY_DAYS, ChronoUnit.DAYS);
        List<File> expiredFiles = indexPort.findExpired(cutoff);

        System.out.println("Found " + expiredFiles.size() + " expired files");

        int deletedCount = 0;
        long freedBytes = 0;

        for (File file : expiredFiles) {
            try {
                System.out.println("Deleting expired file: " + file.getId() +
                        " (last accessed: " + file.getLastAccessedAt() + ")");

                // Удаляем из хранилища
                storagePort.delete(file.getId().value());

                // Удаляем из индекса
                indexPort.delete(file.getId());

                // Уведомляем
                notifierPort.notifyFileExpired(file);

                deletedCount++;
                freedBytes += file.getSizeBytes();

            } catch (Exception e) {
                System.err.println("Failed to delete file " + file.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("=== Cleanup completed: deleted " + deletedCount +
                " files, freed " + formatBytes(freedBytes) + " ===");

        return new CleanupResult(deletedCount, freedBytes);
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}
