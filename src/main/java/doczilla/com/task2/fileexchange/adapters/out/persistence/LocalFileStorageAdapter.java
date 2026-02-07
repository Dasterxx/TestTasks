package doczilla.com.task2.fileexchange.adapters.out.persistence;

import doczilla.com.task2.fileexchange.adapters.config.AppConfig;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.repository.FileStoragePort;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Optional;

/**
 * Реализация хранилища файлов на локальной файловой системе.
 * Пути берутся из AppConfig - относительные, работают везде.
 */
public final class LocalFileStorageAdapter implements FileStoragePort {

    private final Path baseDirectory;

    public LocalFileStorageAdapter() {
        // Берём путь из конфигурации!
        this.baseDirectory = AppConfig.getUploadPath();
        System.out.println("File storage initialized at: " + this.baseDirectory);
    }

    /**
     * Для тестов - можно передать кастомный путь.
     */
    public LocalFileStorageAdapter(Path customPath) {
        this.baseDirectory = customPath.toAbsolutePath().normalize();
        ensureDirectoryExists();
        System.out.println("File storage initialized at: " + this.baseDirectory);
    }

    @Override
    public String store(FileId fileId, byte[] content) {
        try {
            // Иерархия: uploads/ab/cd/abcdef123... (для больших объёмов)
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

            return id;

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file: " + fileId, e);
        }
    }

    @Override
    public Optional<byte[]> retrieve(String storageId) {
        try {
            Path path = resolvePath(storageId);
            if (!Files.exists(path)) {
                return Optional.empty();
            }
            return Optional.of(Files.readAllBytes(path));
        } catch (IOException e) {
            System.err.println("Failed to retrieve file: " + storageId);
            return Optional.empty();
        }
    }

    @Override
    public void delete(String storageId) {
        try {
            Path path = resolvePath(storageId);
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                System.out.println("Deleted file: " + storageId);
                // Пробуем удалить пустые поддиректории
                cleanupEmptyDirs(path.getParent());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete file: " + storageId, e);
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

    private void ensureDirectoryExists() {
        if (!baseDirectory.toFile().exists()) {
            baseDirectory.toFile().mkdirs();
        }
    }

    /**
     * Удаляет пустые директории после удаления файла.
     */
    private void cleanupEmptyDirs(Path dir) {
        try {
            if (dir != null && dir.startsWith(baseDirectory)) {
                if (dir.toFile().isDirectory() && dir.toFile().list().length == 0) {
                    Files.deleteIfExists(dir);
                    cleanupEmptyDirs(dir.getParent());
                }
            }
        } catch (IOException e) {
            // Игнорируем - не критично
        }
    }
}