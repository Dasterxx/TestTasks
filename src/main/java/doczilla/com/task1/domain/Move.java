package doczilla.com.task1.domain;

public record Move(int fromTube, int toTube) {
    public Move {
        if (fromTube < 0 || toTube < 0) {
            throw new IllegalArgumentException("Indices must be non-negative");
        }
        if (fromTube == toTube) {
            throw new IllegalArgumentException("Cannot move to same tube");
        }
    }

    @Override
    public String toString() {
        return String.format("(%2d, %2d)", fromTube, toTube);
    }
}