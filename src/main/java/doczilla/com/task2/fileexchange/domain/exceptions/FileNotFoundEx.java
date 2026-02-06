package doczilla.com.task2.fileexchange.domain.exceptions;

import doczilla.com.task2.fileexchange.domain.model.FileId;

public class FileNotFoundEx extends RuntimeException {

    private final FileId fileId;
    private final String fileIdString;

    public FileNotFoundEx(FileId fileId) {
        super("File not found: " + fileId);
        this.fileId = fileId;
        this.fileIdString = null;
    }

    public FileNotFoundEx(String fileIdString) {
        super("File not found: " + fileIdString);
        this.fileId = null;
        this.fileIdString = fileIdString;
    }

    public FileId getFileId() {
        return fileId;
    }
}
