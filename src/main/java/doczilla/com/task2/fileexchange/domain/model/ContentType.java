package doczilla.com.task2.fileexchange.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public final class ContentType implements Serializable {
    private static final Pattern VALID_PATTERN =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9-]+/[a-zA-Z0-9.+-]+$");

    @Serial
    private static final long serialVersionUID = 1L;
    private final String value;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            // Документы
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "text/csv",
            // Изображения
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/svg+xml",
            // Архивы
            "application/zip",
            "application/x-rar-compressed",
            "application/x-7z-compressed",
            "application/gzip",
            "application/x-tar",
            // Аудио/Видео
            "audio/mpeg", "audio/wav", "audio/ogg", "audio/mp4",
            "video/mp4", "video/webm", "video/avi", "video/mpeg",
            // Другое
            "application/octet-stream"  // Для неизвестных типов
    );

    // Заблокированные типы (опасные)
    private static final Set<String> BLOCKED_TYPES = Set.of(
            "application/x-msdownload",
            "application/x-executable",
            "application/x-msdos-program",
            "application/javascript",
            "text/html",
            "application/xhtml+xml",
            "application/x-sh",
            "application/x-php",
            "application/x-python-code",
            "application/java-archive"
    );

    private ContentType(String value) {
        String normalized = value != null ? value.toLowerCase().trim() : "";

        if (BLOCKED_TYPES.contains(normalized)) {
            throw new SecurityException("Content type not allowed: " + normalized);
        }

        if (!normalized.isEmpty() && !normalized.equals("application/octet-stream")) {
            if (!ALLOWED_TYPES.contains(normalized)) {
                throw new IllegalArgumentException(
                        "File type not allowed: " + normalized +
                                ". Allowed types: documents (PDF, DOCX, XLSX), images (JPEG, PNG, GIF), " +
                                "archives (ZIP, RAR, 7Z), audio/video (MP3, MP4, WAV, AVI)"
                );
            }
        }

        if (!isValid(normalized)) {
            throw new IllegalArgumentException("Invalid content type: " + value);
        }

        this.value = normalized.isEmpty() ? "application/octet-stream" : normalized;
    }

    public static ContentType of(String value) {
        return new ContentType(value);
    }

    public static ContentType applicationOctetStream() {
        return new ContentType("application/octet-stream");
    }

    private boolean isValid(String value) {
        return VALID_PATTERN.matcher(value).matches();
    }

    public String value() {
        return value;
    }

    public boolean isImage() {
        return value.startsWith("image/");
    }

    public boolean isText() {
        return value.startsWith("text/") || value.equals("application/json");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof ContentType other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}