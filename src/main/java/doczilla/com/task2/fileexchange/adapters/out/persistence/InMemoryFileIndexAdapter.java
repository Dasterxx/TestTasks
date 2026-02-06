package doczilla.com.task2.fileexchange.adapters.out.persistence;

import doczilla.com.task2.fileexchange.domain.model.File;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class InMemoryFileIndexAdapter implements FileIndexPort {

    private final ConcurrentHashMap<FileId, File> storage = new ConcurrentHashMap<>();

    @Override
    public FileId save(File file) {
        storage.put(file.getId(), file);
        return file.getId();
    }

    @Override
    public Optional<File> findById(FileId id) {
        return Optional.ofNullable(storage.get(id))
                .filter(f -> !f.isDeleted());
    }

    @Override
    public List<File> findByOwner(UserId owner) {
        return storage.values().stream()
                .filter(f -> owner.equals(f.getOwnerId()))
                .filter(f -> !f.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<File> findExpired(Instant before) {
        return storage.values().stream()
                .filter(f -> f.getLastAccessedAt().isBefore(before))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(FileId id) {
        storage.computeIfPresent(id, (k, file) -> {
            file.markDeleted();
            return file;
        });
    }

    @Override
    public void update(File file) {
        storage.put(file.getId(), file);
    }

    @Override
    public long count() {
        return storage.values().stream().filter(f -> !f.isDeleted()).count();
    }

    @Override
    public long totalSize() {
        return storage.values().stream()
                .filter(f -> !f.isDeleted())
                .mapToLong(File::getSizeBytes)
                .sum();
    }
}
