package doczilla.com.task2.fileexchange.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object для токена доступа.
 */
public final class AccessToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String value;

    private AccessToken(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
        this.value = value;
    }

    public static AccessToken generate() {
        return new AccessToken(UUID.randomUUID().toString());
    }

    public static AccessToken of(String value) {
        return new AccessToken(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof AccessToken other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        // Маскируем токен в логах
        return value.substring(0, Math.min(4, value.length())) + "***";
    }
}
