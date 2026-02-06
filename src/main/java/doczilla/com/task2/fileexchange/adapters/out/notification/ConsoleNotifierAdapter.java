package doczilla.com.task2.fileexchange.adapters.out.notification;

import doczilla.com.task2.fileexchange.application.ports.out.FileNotifierPort;
import doczilla.com.task2.fileexchange.domain.model.File;

/**
 * Реализация FileNotifierPort - вывод в консоль.
 */
public class ConsoleNotifierAdapter implements FileNotifierPort {

    @Override
    public void notifyFileUploaded(File file) {
        System.out.printf("[NOTIFY] File uploaded: %s (id=%s, size=%d bytes, owner=%s)%n",
                file.getName().value(),
                file.getId().toFullString(),
                file.getSizeBytes(),
                file.getOwnerId() != null ? file.getOwnerId() : "anonymous"
        );
    }

    @Override
    public void notifyFileDownloaded(File file, int downloadCount) {
        System.out.printf("[NOTIFY] File downloaded: %s (total downloads: %d)%n",
                file.getName().value(),
                downloadCount
        );
    }

    @Override
    public void notifyFileExpired(File file) {
        System.out.printf("[NOTIFY] File expired and removed: %s%n",
                file.getName().value()
        );
    }
}

