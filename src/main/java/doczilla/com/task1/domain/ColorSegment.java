package doczilla.com.task1.domain;

/**
 * Value object representing a pourable segment
 */
public record ColorSegment(Color color, int count) {
    public ColorSegment {
        if (count <= 0) throw new IllegalArgumentException("Count must be positive");
        if (color.isEmpty()) throw new IllegalArgumentException("Color cannot be empty");
    }
}