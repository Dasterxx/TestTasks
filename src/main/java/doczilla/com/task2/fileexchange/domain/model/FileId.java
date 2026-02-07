package doczilla.com.task2.fileexchange.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public final class FileId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String value;

    private FileId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FileId cannot be empty");
        }
        if (value.length() < 16) {
            throw new IllegalArgumentException("FileId too short");
        }
        this.value = value;
    }

    public static FileId generate() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return new FileId(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
    }

    public static FileId of(String value) {
        return new FileId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof FileId other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.substring(0, 4) + "...";
    }

    public String toFullString() {
        return value;
    }
}