package doczilla.com.task1.domain;

public record Color(int value) {
    public static final Color EMPTY = new Color(-1);

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    @Override
    public String toString() {
        return isEmpty() ? "·" : String.valueOf(value);
    }
}
