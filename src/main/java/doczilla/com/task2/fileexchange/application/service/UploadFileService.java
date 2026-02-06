package doczilla.com.task2.fileexchange.application.service;

import doczilla.com.task2.fileexchange.application.ports.in.UploadFileUseCase;
import doczilla.com.task2.fileexchange.application.ports.out.AuditLogPort;
import doczilla.com.task2.fileexchange.application.ports.out.FileNotifierPort;
import doczilla.com.task2.fileexchange.domain.model.ContentType;
import doczilla.com.task2.fileexchange.domain.model.File;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.FileName;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;

import java.util.Objects;

public final class UploadFileService implements UploadFileUseCase {

    private final FileStoragePort storagePort;
    private final FileIndexPort indexPort;
    private final FileNotifierPort notifierPort;
    private final AuditLogPort auditLogPort;

    public UploadFileService(
            FileStoragePort storagePort,
            FileIndexPort indexPort,
            FileNotifierPort notifierPort,
            AuditLogPort auditLogPort
    ) {
        this.storagePort = Objects.requireNonNull(storagePort);
        this.indexPort = Objects.requireNonNull(indexPort);
        this.notifierPort = Objects.requireNonNull(notifierPort);
        this.auditLogPort = Objects.requireNonNull(auditLogPort);
    }

    @Override
    public FileId upload(UploadFileCommand command) {
        // 1. Создаем доменный объект (валидация здесь!)
        File file = File.create(
                FileName.of(command.fileName()),
                ContentType.of(command.contentType()),
                command.size(),
                command.uploadedBy()
        );

        // 2. Сохраняем бинарные данные
        String storageId = storagePort.store(file.getId(), command.content());

        // 3. Сохраняем метаданные
        FileId id = indexPort.save(file);

        // 4. Публикация событий (side effects)
        notifierPort.notifyFileUploaded(file);
        auditLogPort.log("UPLOAD", file.getId(), command.uploadedBy());

        return id;
    }
}