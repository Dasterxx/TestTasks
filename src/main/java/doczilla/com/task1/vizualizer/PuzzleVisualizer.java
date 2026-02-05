package doczilla.com.task1.vizualizer;

import doczilla.com.task1.domain.Color;
import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;
import doczilla.com.task1.domain.Tube;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Presentation Layer - State visualization with encoding handling
 */
public class PuzzleVisualizer {
    private final Function<Color, String> colorFormatter;
    private final PrintStream out;



    // Factory method for default formatter (ASCII safe)
    private static Function<Color, String> createDefaultFormatter() {
        return color -> {
            if (color.isEmpty()) return "[ ]";
            return switch (color.value()) {
                case 1 -> "[R]";
                case 2 -> "[B]";
                case 3 -> "[G]";
                case 4 -> "[Y]";
                case 5 -> "[P]";
                case 6 -> "[O]";
                case 7 -> "[W]";
                case 8 -> "[N]";
                case 9 -> "[C]";
                case 10 -> "[M]";
                case 11 -> "[L]";
                case 12 -> "[K]";
                default -> String.format("[%d]", color.value());
            };
        };
    }

    public static Function<Color, String> createEmojiFormatter() {
        return color -> switch (color.value()) {
            case -1 -> " · ";
            case 0 -> "🔴";
            case 1 -> "🔵";
            case 2 -> "🟢";
            case 3 -> "🟡";
            case 4 -> "🟣";
            case 5 -> "🟠";
            case 6 -> "⚪";
            case 7 -> "🟤";
            case 8 -> "🔷";
            case 9 -> "💚";
            case 10 -> "💛";
            case 11 -> "💜";
            default -> String.format("%2d ", color.value());
        };
    }

    private static PrintStream createUTF8PrintStream() {
        try {
            return new PrintStream(System.out, true, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return System.out;
        }
    }

    // Builder-style method to create new instance with emoji formatter
    public PuzzleVisualizer withEmojiFormatter() {
        return new PuzzleVisualizer(createEmojiFormatter(), out);
    }

    // Builder-style method to create new instance with custom formatter
    public PuzzleVisualizer withFormatter(Function<Color, String> formatter) {
        return new PuzzleVisualizer(formatter, out);
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

    public void printSolution(Solution solution, PuzzleState initialState) {
        out.println("TIME TO SOLVE IN MINUTES :" + solution.moves().size() + " STEPS");
        out.println("SEARCHED STEPS: " + solution.stepsExplored());
        out.println("\nSUBSEQUENCE STEPS:");

        for (int i = 0; i < solution.moves().size(); i++) {
            out.print(solution.moves().get(i));
            if ((i + 1) % 8 == 0) out.println();
            else out.print(" ");
        }
        out.println("\n");
    }

    public void animateSolution(Solution solution, PuzzleState initialState) {
        PuzzleState current = initialState;
        out.println("START STATUS:");
        printState(current);

        for (int i = 0; i < solution.moves().size(); i++) {
            Move move = solution.moves().get(i);
            Optional<PuzzleState> next = current.apply(move);

            if (next.isEmpty()) {
                out.println("MOVEMENT FAILED: " + move);
                return;
            }

            current = next.get();
            out.println("Ход " + (i + 1) + ": " + move);
            printState(current);
        }

        out.println(current.isSolved() ? "✓ DONE!" : "✗ FAIL");
    }

    public void printSuccess() {
        out.println("[OK] Решение верифицировано успешно!");
    }

    public void printFailure() {
        out.println("[FAIL] Конечное состояние не является решением");
    }


    public PuzzleVisualizer() {
        this(createDefaultFormatter(), createUTF8PrintStream());
    }

    public PuzzleVisualizer(Function<Color, String> colorFormatter) {
        this(colorFormatter, createUTF8PrintStream());
    }

    public PuzzleVisualizer(Function<Color, String> colorFormatter, PrintStream out) {
        this.colorFormatter = colorFormatter;
        this.out = out;
    }
}