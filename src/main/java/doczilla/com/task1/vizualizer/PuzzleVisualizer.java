package doczilla.com.task1.vizualizer;

import doczilla.com.task1.domain.Color;
import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Tube;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

/**
 * Presentation Layer - State visualization with encoding handling
 */
public class PuzzleVisualizer {
    private final Function<Color, String> colorFormatter;
    private final PrintStream out;

    private static Function<Color, String> createDefaultFormatter() {
        return color -> {
            if (color.isEmpty()) return "[ ]";
            return switch (color.value()) {
                case 1 -> "[A]";
                case 2 -> "[B]";
                case 3 -> "[C]";
                case 4 -> "[D]";
                case 5 -> "[E]";
                case 6 -> "[F]";
                case 7 -> "[G]";
                case 8 -> "[H]";
                case 9 -> "[I]";
                case 10 -> "[J]";
                case 11 -> "[K]";
                case 12 -> "[L]";
                default -> String.format("[%d]", color.value());
            };
        };
    }

    private static PrintStream createUTF8PrintStream() {
        return new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }

    public String visualize(PuzzleState state) {
        StringBuilder sb = new StringBuilder();
        int capacity = state.getCapacity();
        List<Tube> tubes = state.getTubes();

        for (int level = capacity - 1; level >= 0; level--) {
            for (Tube tube : tubes) {
                List<Color> display = tube.toDisplayArray();
                Color c = display.get(level);
                sb.append(colorFormatter.apply(c)).append(" ");
            }
            sb.append("\n");
        }

        int separatorLength = tubes.size() * 4;
        sb.append("-".repeat(separatorLength)).append("\n");

        for (int i = 0; i < tubes.size(); i++) {
            sb.append(String.format("%2d ", i));
        }
        sb.append("\n");

        return sb.toString();
    }

    public void printState(PuzzleState state) {
        out.print(visualize(state));
    }

    public PuzzleVisualizer() {
        this(createDefaultFormatter(), createUTF8PrintStream());
    }

    public PuzzleVisualizer(Function<Color, String> colorFormatter, PrintStream out) {
        this.colorFormatter = colorFormatter;
        this.out = out;
    }
}