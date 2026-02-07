package doczilla.com.task2.fileexchange.adapters.out.persistence;

import doczilla.com.task2.fileexchange.adapters.config.AppConfig;
import doczilla.com.task2.fileexchange.domain.model.File;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory индекс с персистентностью в файл.
 * Путь к файлу индекса берётся из AppConfig.
 */
public final class InMemoryFileIndexAdapter implements FileIndexPort {

    private final ConcurrentHashMap<FileId, File> storage = new ConcurrentHashMap<>();
    private final Path indexFile;
    private volatile boolean dirty = false;

    public InMemoryFileIndexAdapter() {
        // Берём путь из конфигурации!
        this.indexFile = AppConfig.getIndexFilePath();
        load();

        // Автосохранение каждые 30 секунд
        startAutoSave();
    }

    /**
     * Для тестов - кастомный путь.
     */
    public InMemoryFileIndexAdapter(Path customIndexPath) {
        this.indexFile = customIndexPath;
        load();
    }

    @Override
    public FileId save(File file) {
        storage.put(file.getId(), file);
        dirty = true;
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
                .toList();
    }

    @Override
    public List<File> findExpired(Instant before) {
        return storage.values().stream()
                .filter(f -> !f.isDeleted())
                .filter(f -> f.getLastAccessedAt().isBefore(before))
                .toList();
    }

    @Override
    public void delete(FileId id) {
        File file = storage.get(id);
        if (file != null) {
            file.markDeleted();
            dirty = true;
        }
    }

    @Override
    public void update(File file) {
        storage.put(file.getId(), file);
        dirty = true;
    }

    @Override
    public long count() {
        return storage.values().stream()
                .filter(f -> !f.isDeleted())
                .count();
    }

    @Override
    public long totalSize() {
        return storage.values().stream()
                .filter(f -> !f.isDeleted())
                .mapToLong(File::getSizeBytes)
                .sum();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        java.io.File file = indexFile.toFile();
        if (!file.exists()) {
            System.out.println("Index file not found, starting fresh: " + indexFile);
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {

            Map<FileId, File> loaded = (Map<FileId, File>) ois.readObject();
            storage.putAll(loaded);
            System.out.println("Loaded " + loaded.size() + " files from index: " + indexFile);

        } catch (Exception e) {
            System.err.println("Failed to load index, starting fresh: " + e.getMessage());
        }
    }

    public synchronized void save() {
        if (!dirty) return;

        try {
            // Создаём директории если нужно
            indexFile.getParent().toFile().mkdirs();

            Path tempFile = indexFile.resolveSibling(indexFile.getFileName() + ".tmp");
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(tempFile.toFile())))) {
                oos.writeObject(new HashMap<>(storage));
            }

            // Атомарная замена
            Files.move(tempFile, indexFile,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);

            dirty = false;
            System.out.println("Index saved: " + storage.size() + " files");

        } catch (IOException e) {
            System.err.println("Failed to save index: " + e.getMessage());
        }
    }

    private void startAutoSave() {
        Thread saver = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(30000); // 30 секунд
                    if (dirty) {
                        save();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        saver.setName("index-auto-save");
        saver.setDaemon(true);
        saver.start();
    }

    // Вызывается при shutdown
    public void shutdown() {
        save();
    }
}