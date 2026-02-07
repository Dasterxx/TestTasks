package doczilla.com.task2.fileexchange.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public final class FileName implements Serializable {
    private static final Pattern INVALID_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");
    private static final int MAX_LENGTH = 255;

    @Serial
    private static final long serialVersionUID = 1L;
    private final String value;
    private final String extension;

    // Заблокированные расширения
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            "exe", "dll", "bat", "cmd", "sh", "bash", "zsh",
            "php", "jsp", "asp", "aspx",
            "py", "pyc", "pyo", "rb", "pl", "cgi",
            "jar", "class", "war", "ear",
            "msi", "dmg", "pkg", "deb", "rpm",
            "com", "scr", "vbs", "js", "wsf", "hta",
            "ps1", "psm1", "psd1"
    );

    private FileName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Filename too long (max " + MAX_LENGTH + " chars)");
        }
        if (INVALID_CHARS.matcher(value).find()) {
            throw new IllegalArgumentException("Invalid characters in filename");
        }

        this.value = sanitize(value);
        this.extension = extractExtension(value);

        // Проверка расширения
        if (BLOCKED_EXTENSIONS.contains(this.extension)) {
            throw new SecurityException(
                    "File extension not allowed: ." + this.extension +
                            ". Allowed: documents, images, archives, audio/video files"
            );
        }
    }

    public static FileName of(String value) {
        return new FileName(value);
    }

    private String sanitize(String input) {
        return input.trim().replaceAll("\\s+", "_");
    }

    private String extractExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1).toLowerCase() : "";
    }

    public String value() { return value; }
    public String extension() { return extension; }

    public boolean hasExtension(String ext) {
        return extension.equalsIgnoreCase(ext);
    }

    public FileName withSuffix(String suffix) {
        int dot = value.lastIndexOf('.');
        String base = dot > 0 ? value.substring(0, dot) : value;
        String newName = base + "_" + suffix +
                (dot > 0 ? value.substring(dot) : "");
        return new FileName(newName);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FileName other && value.equals(other.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() {
        return value;
    }
}