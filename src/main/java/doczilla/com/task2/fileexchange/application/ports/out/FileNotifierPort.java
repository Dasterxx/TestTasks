package doczilla.com.task2.fileexchange.application.ports.out;

import doczilla.com.task2.fileexchange.domain.model.File;

/**
 * Outgoing Port - уведомления о событиях с файлами.
 * Реализация: email, webhook, push-уведомления.
 */
public interface FileNotifierPort {

    /**
     * Уведомление о загрузке нового файла.
     */
    void notifyFileUploaded(File file);

    /**
     * Уведомление о скачивании (опционально).
     */
    default void notifyFileDownloaded(File file, int downloadCount) {
        // По умолчанию ничего не делаем
    }

    /**
     * Уведомление об удалении по истечению срока.
     */
    default void notifyFileExpired(File file) {
        // По умолчанию ничего не делаем
    }
}
