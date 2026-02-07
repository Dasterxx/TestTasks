package doczilla.com.task2.fileexchange.application.service;

import doczilla.com.task2.fileexchange.application.ports.in.DownloadFileUseCase;
import doczilla.com.task2.fileexchange.domain.exceptions.FileNotFoundEx;
import doczilla.com.task2.fileexchange.domain.exceptions.UnauthorizedAccessException;
import doczilla.com.task2.fileexchange.domain.model.File;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;

import java.util.Objects;
import java.util.Optional;

public final class DownloadFileService implements DownloadFileUseCase {

    private final FileIndexPort indexPort;
    private final FileStoragePort storagePort;

    public DownloadFileService(FileIndexPort indexPort, FileStoragePort storagePort) {
        this.indexPort = Objects.requireNonNull(indexPort);
        this.storagePort = Objects.requireNonNull(storagePort);
    }

    @Override
    public Optional<FileResult> download(FileId fileId, UserId requestedBy) {
        System.out.println("=== DEBUG ===");
        System.out.println("fileId: " + fileId);
        System.out.println("requestedBy: " + requestedBy);

        File file = indexPort.findById(fileId)
                .orElseThrow(() -> new FileNotFoundEx(fileId));

        System.out.println("Found file: " + file.getId());
        System.out.println("file.ownerId: " + file.getOwnerId());
        System.out.println("file.isAccessibleBy(requestedBy): " + file.isAccessibleBy(requestedBy));

        if (!file.isAccessibleBy(requestedBy)) {
            System.out.println("ACCESS DENIED!");
            throw new UnauthorizedAccessException(fileId);
        }

        System.out.println("Access granted, recording download...");
        file.recordDownload();
        System.out.println("Download recorded, updating index...");
        indexPort.update(file);
        System.out.println("Index updated, retrieving content...");

        byte[] content = storagePort.retrieve(fileId.value())
                .orElseThrow(() -> new FileNotFoundEx(fileId));

        System.out.println("Content retrieved, returning result");
        System.out.println("=== END DEBUG ===");

        return Optional.of(new FileResult(
                file.getName().value(),
                file.getContentType().toString(),
                content,
                file.getSizeBytes(),
                file.getDownloadCount()
        ));
    }
}