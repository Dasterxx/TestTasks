package doczilla.com.task2.fileexchange.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ContentType implements Serializable {
    private static final Pattern VALID_PATTERN =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9-]+/[a-zA-Z0-9.+-]+$");

    @Serial
    private static final long serialVersionUID = 1L;
    private final String value;

    private ContentType(String value) {
        String normalized = value != null ? value.toLowerCase().trim() : "";
        if (!isValid(normalized)) {
            throw new IllegalArgumentException("Invalid content type: " + value);
        }
        this.value = normalized;
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
