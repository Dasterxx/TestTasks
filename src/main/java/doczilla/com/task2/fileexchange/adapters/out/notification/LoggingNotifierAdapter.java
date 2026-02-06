package doczilla.com.task2.fileexchange.adapters.out.notification;

import doczilla.com.task2.fileexchange.application.ports.out.FileNotifierPort;
import doczilla.com.task2.fileexchange.domain.model.File;

import java.util.logging.Logger;

public class LoggingNotifierAdapter implements FileNotifierPort {

    private static final Logger LOGGER =
            Logger.getLogger(LoggingNotifierAdapter.class.getName());

    @Override
    public void notifyFileUploaded(File file) {
        LOGGER.info(() -> String.format(
                "File uploaded: %s by %s",
                file.getName().value(),
                file.getOwnerId()
        ));
    }

    @Override
    public void notifyFileDownloaded(File file, int downloadCount) {
        LOGGER.fine(() -> String.format(
                "File downloaded: %s (count: %d)",
                file.getName().value(),
                downloadCount
        ));
    }

    @Override
    public void notifyFileExpired(File file) {
        LOGGER.info(() -> String.format(
                "File expired: %s",
                file.getName().value()
        ));
    }
}
