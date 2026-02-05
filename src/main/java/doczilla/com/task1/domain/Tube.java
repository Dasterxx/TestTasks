package doczilla.com.task1.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Tube {
    private final List<Color> contents;
    private final int capacity;

    private Tube(List<Color> contents, int capacity) {
        this.contents = List.copyOf(contents);
        this.capacity = capacity;
    }

    public static Tube createEmpty(int capacity) {
        return new Tube(Collections.emptyList(), capacity);
    }

    public static Tube of(List<Color> contents, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        // Создаём копию, чтобы избежать мутаций
        List<Color> copy = new ArrayList<>(contents);
        if (copy.size() > capacity) {
            throw new IllegalArgumentException("Contents exceed capacity");
        }
        return new Tube(copy, capacity);
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public boolean isFull() {
        return contents.size() == capacity;
    }

    public int getEmptySpace() {
        return capacity - contents.size();
    }

    public boolean isUniform() {
        if (isEmpty()) return true;
        Color first = contents.get(0);
        return contents.stream().allMatch(c -> c.equals(first));
    }
    /**
     * Returns top color segment (color + count)
     */
    public Optional<ColorSegment> getTopSegment() {
        if (isEmpty()) return Optional.empty();

        Color topColor = contents.get(contents.size() - 1);
        int count = 0;
        for (int i = contents.size() - 1; i >= 0; i--) {
            if (contents.get(i).equals(topColor)) {
                count++;
            } else {
                break;
            }
        }
        return Optional.of(new ColorSegment(topColor, count));
    }

    public Color getTopColor() {
        return getTopSegment().map(ColorSegment::color).orElse(Color.EMPTY);
    }

    /**
     * Attempts to pour into this tube from another segment
     */
    public Optional<Tube> pourIn(ColorSegment segment) {
        if (isFull()) return Optional.empty();

        // Can only pour if empty or same color on top
        if (!isEmpty() && !getTopColor().equals(segment.color())) {
            return Optional.empty();
        }

        int pourAmount = Math.min(segment.count(), getEmptySpace());
        List<Color> newContents = new ArrayList<>(contents);

        for (int i = 0; i < pourAmount; i++) {
            newContents.add(segment.color());
        }

        return Optional.of(new Tube(newContents, capacity));
    }

    /**
     * Removes top segment from tube
     */
    public Optional<Tube> pourOut(int amount) {
        if (amount > contents.size()) return Optional.empty();

        List<Color> newContents = contents.subList(0, contents.size() - amount);
        return Optional.of(new Tube(newContents, capacity));
    }

    public List<Color> getContents() {
        return contents;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Color> toDisplayArray() {
        List<Color> display = new ArrayList<>(contents);
        while (display.size() < capacity) {
            display.add(Color.EMPTY);
        }
        return display;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tube tube)) return false;
        return capacity == tube.capacity && contents.equals(tube.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contents, capacity);
    }
}
