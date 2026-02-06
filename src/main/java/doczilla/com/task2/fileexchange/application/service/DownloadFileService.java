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
        File file = indexPort.findById(fileId)
                .orElseThrow(() -> new FileNotFoundEx(fileId));

        if (!file.isAccessibleBy(requestedBy)) {
            throw new UnauthorizedAccessException(fileId);
        }

        file.recordDownload();
        indexPort.update(file);

        byte[] content = storagePort.retrieve(fileId.value())
                .orElseThrow(() -> new FileNotFoundEx(fileId));

        return Optional.of(new FileResult(
                file.getName().value(),
                file.getContentType().toString(),
                content,
                file.getSizeBytes(),
                file.getDownloadCount()
        ));
    }
}