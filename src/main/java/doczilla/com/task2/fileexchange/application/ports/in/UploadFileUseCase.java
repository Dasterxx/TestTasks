package doczilla.com.task2.fileexchange.application.ports.in;

import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;

public interface UploadFileUseCase {

    FileId upload(UploadFileCommand command);

    /**
     * Command object - неизменяемые входные данные.
     * java-21 :)
     */
    record UploadFileCommand(
            String fileName,
            String contentType,
            byte[] content,
            long size,
            UserId uploadedBy
    ) {
        public UploadFileCommand {
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("Filename required");
            }
            if (content == null || content.length == 0) {
                throw new IllegalArgumentException("Content required");
            }
            if (size != content.length) {
                throw new IllegalArgumentException("Size mismatch");
            }
        }
    }
}
