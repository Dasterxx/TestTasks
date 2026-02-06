package doczilla.com.task2.fileexchange.adapters.out.persistence;

import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public final class LocalFileStorageAdapter implements FileStoragePort {

    private final Path baseDirectory;

    public LocalFileStorageAdapter(String baseDirectory) throws IOException {
        this.baseDirectory = Path.of(baseDirectory);
        Files.createDirectories(this.baseDirectory);
    }

    @Override
    public String store(FileId fileId, byte[] content) {
        try {
            // Иерархия: uploads/ab/cd/abcdef123... (для больших объемов)
            String id = fileId.value();
            String subdir1 = id.substring(0, 2);
            String subdir2 = id.substring(2, 4);

            Path dir = baseDirectory.resolve(subdir1).resolve(subdir2);
            Files.createDirectories(dir);

            Path filePath = dir.resolve(id);

            // Атомарная запись через temp file
            Path temp = filePath.resolveSibling(id + ".tmp");
            Files.write(temp, content, StandardOpenOption.CREATE_NEW);
            Files.move(temp, filePath, StandardCopyOption.ATOMIC_MOVE);

            return id; // storageId совпадает с fileId для простоты

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file", e);
        }
    }

    @Override
    public Optional<byte[]> retrieve(String storageId) {
        try {
            Path path = resolvePath(storageId);
            if (!Files.exists(path)) return Optional.empty();
            return Optional.of(Files.readAllBytes(path));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String storageId) {
        try {
            Files.deleteIfExists(resolvePath(storageId));
            // Cleanup empty directories...
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean exists(String storageId) {
        return Files.exists(resolvePath(storageId));
    }

    private Path resolvePath(String storageId) {
        String subdir1 = storageId.substring(0, 2);
        String subdir2 = storageId.substring(2, 4);
        return baseDirectory.resolve(subdir1).resolve(subdir2).resolve(storageId);
    }
}
