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
        try {
            FileName fileName = FileName.of(command.fileName());

            ContentType contentType = ContentType.of(command.contentType());

            File file = File.create(fileName, contentType, command.size(), command.uploadedBy());

            String storageId = storagePort.store(file.getId(), command.content());

            FileId id = indexPort.save(file);

            notifierPort.notifyFileUploaded(file);
            auditLogPort.log("UPLOAD", file.getId(), command.uploadedBy());

            return id;
        } catch (SecurityException e) {
            throw new SecurityException("UPLOAD_REJECTED: " + e.getMessage());
        }
    }
}