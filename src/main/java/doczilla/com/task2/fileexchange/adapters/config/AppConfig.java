// adapters/config/AppConfig.java
package doczilla.com.task2.fileexchange.adapters.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Централизованная конфигурация приложения.
 */
public final class AppConfig {

    // Приватный конструктор - утилитный класс
    private AppConfig() {}

    // === Server ===
    public static final int SERVER_PORT = 8080;
    public static final String SERVER_HOST = "localhost";

    // === Storage ===
    // Относительный путь: создаётся в директории запуска приложения
    public static final String UPLOAD_DIR = "uploads";
    public static final String DATA_DIR = "data";
    public static final String INDEX_FILE = "file-index.db";

    // === File Limits ===
    public static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100 MB
    public static final int EXPIRY_DAYS = 30;

    // === Cleanup ===
    public static final long CLEANUP_INTERVAL_HOURS = 24;
    public static final boolean CLEANUP_ENABLED = true;

    /**
     * Получает абсолютный путь к директории загрузок.
     * Создаёт директорию если не существует.
     */
    public static Path getUploadPath() {
        Path path = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        ensureDirectoryExists(path);
        return path;
    }

    /**
     * Получает абсолютный путь к директории данных (индексы, логи).
     */
    public static Path getDataPath() {
        Path path = Paths.get(DATA_DIR).toAbsolutePath().normalize();
        ensureDirectoryExists(path);
        return path;
    }

    /**
     * Получает путь к файлу индекса.
     */
    public static Path getIndexFilePath() {
        return getDataPath().resolve(INDEX_FILE);
    }

    /**
     * Проверяет и создаёт директорию если нужно.
     */
    private static void ensureDirectoryExists(Path path) {
        if (!path.toFile().exists()) {
            boolean created = path.toFile().mkdirs();
            if (created) {
                System.out.println("Created directory: " + path);
            }
        }
    }

    /**
     * Печатает конфигурацию при старте.
     */
    public static void printConfig() {
        System.out.println("=== Application Configuration ===");
        System.out.println("Server: http://" + SERVER_HOST + ":" + SERVER_PORT);
        System.out.println("Upload directory: " + getUploadPath());
        System.out.println("Data directory: " + getDataPath());
        System.out.println("Max file size: " + formatSize(MAX_FILE_SIZE));
        System.out.println("File expiry: " + EXPIRY_DAYS + " days");
        System.out.println("Cleanup enabled: " + CLEANUP_ENABLED);
        System.out.println("=================================");
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}
