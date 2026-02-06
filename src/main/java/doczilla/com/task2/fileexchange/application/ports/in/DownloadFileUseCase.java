package doczilla.com.task2.fileexchange.application.ports.in;

import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.util.Optional;

public interface DownloadFileUseCase {

    Optional<FileResult> download(FileId fileId, UserId requestedBy);

    record FileResult(
            String fileName,
            String contentType,
            byte[] content,
            long size,
            int totalDownloads
    ) {}
}

