package doczilla.com.task2.fileexchange.domain.exceptions;

import doczilla.com.task2.fileexchange.domain.model.FileId;

public class UnauthorizedAccessException extends RuntimeException {

    private final FileId fileId;

    public UnauthorizedAccessException(FileId fileId) {
        super("Access denied to file: " + fileId);
        this.fileId = fileId;
    }

    public FileId getFileId() {
        return fileId;
    }
}

