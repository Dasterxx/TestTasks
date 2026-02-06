package doczilla.com.task2.fileexchange.domain.repository;

import doczilla.com.task2.fileexchange.domain.model.FileId;

import java.util.Optional;

public interface FileStoragePort {

    /**
     * Сохраняет бинарные данные файла.
     * @return идентификатор хранилища (может отличаться от FileId)
     * @throws StorageException при ошибке записи
     */
    String store(FileId fileId, byte[] content);

    /**
     * Получает бинарные данные по идентификатору.
     */
    Optional<byte[]> retrieve(String storageId);

    /**
     * Удаляет данные.
     */
    void delete(String storageId);

    /**
     * Проверяет существование.
     */
    boolean exists(String storageId);
}
