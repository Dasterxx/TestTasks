package doczilla.com.task2.fileexchange.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class UserId {
    private final UUID value;

    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(String uuid) {
        return new UserId(UUID.fromString(uuid));
    }

    public static UserId of(UUID uuid) {
        return new UserId(uuid);
    }

    public UUID value() {
        return value;
    }

    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof UserId other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

