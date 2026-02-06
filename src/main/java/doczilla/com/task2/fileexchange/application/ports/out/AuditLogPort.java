package doczilla.com.task2.fileexchange.application.ports.out;

import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;

/**
 * Outgoing Port - аудит операций с файлами.
 * Реализация: логирование в консоль/файл/БД.
 */
public interface AuditLogPort {

    /**
     * Логирует операцию.
     *
     * @param action тип действия (UPLOAD, DOWNLOAD, DELETE)
     * @param targetId идентификатор файла
     * @param userId кто выполнил (null для анонима)
     */
    void log(String action, FileId targetId, UserId userId);

    /**
     * Логирует с дополнительными деталями.
     */
    default void log(String action, FileId targetId, UserId userId, String details) {
        log(action, targetId, userId);
    }
}