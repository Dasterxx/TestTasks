package doczilla.com.task2.fileexchange.domain.repository;

import doczilla.com.task2.fileexchange.domain.model.File;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FileIndexPort {

    FileId save(File file);

    Optional<File> findById(FileId id);

    List<File> findByOwner(UserId owner);

    List<File> findExpired(Instant before);

    void delete(FileId id);

    void update(File file);

    long count();

    long totalSize();
}