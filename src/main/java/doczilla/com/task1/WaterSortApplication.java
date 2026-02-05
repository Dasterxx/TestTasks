package doczilla.com.task1;

import doczilla.com.task1.domain.*;
import doczilla.com.task1.service.PuzzleSolver;
import doczilla.com.task1.strategy.BFSStrategy;
import doczilla.com.task1.util.ValidConf;
import doczilla.com.task1.vizualizer.PuzzleVisualizer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaterSortApplication {
    private static final ValidConf ValidConfig = new ValidConf();

    public static void main(String[] args) {

        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.stdout.encoding", "UTF-8");

        int capacity = 4;

        List<List<Color>> validConfig = ValidConfig.generateValidConfiguration(12,2,4);
        //List<List<Color>> config = createExampleConfiguration();

        // ВАЛИДАЦИЯ входных данных
        if (!validateConfiguration(validConfig, capacity)) {
            System.out.println("INVALID CONFIGURATION");
            return;
        }

        PuzzleState initialState = PuzzleState.create(validConfig, capacity);

        // Проверяем, не решена ли уже
        if (initialState.isSolved()) {
            System.out.println("ALREADY SOLVED");
            return;
        }

        var visualizer = new PuzzleVisualizer();
        System.out.println("START STATUS:");
        visualizer.printState(initialState);

        // Пробуем сначала эвристику (быстрее), потом BFS
        System.out.println("SOLVING WITH HEURISTIC...");
        var solver = PuzzleSolver.heuristic();

        long start = System.currentTimeMillis();
        var solution = solver.solve(initialState);
        long time = System.currentTimeMillis() - start;

        if (solution.isEmpty()) {
            System.out.println("HEURISTIC FAILED, TRYING BFS...");
            start = System.currentTimeMillis();
            solution = new BFSStrategy(100_000_000).solve(initialState);
            time = System.currentTimeMillis() - start;
        }

        System.out.println("TIME FOR SOLVE: " + time + " MS");

        solution.ifPresentOrElse(
                sol -> {
                    visualizer.printSolution(sol, initialState);
                    System.out.println(sol.finalState().isSolved() ? "[OK]" : "[FAIL]");

                    System.out.println("\nFINAL STATE:");
                    visualizer.printState(sol.finalState());
                },
                () -> {
                    System.out.println("NO SOLUTION FOUND");
                    printDiagnostics(initialState);
                }
        );
    }

    private static boolean validateConfiguration(List<List<Color>> config, int capacity) {
        Map<Color, Integer> counts = new HashMap<>();

        for (List<Color> tube : config) {
            if (tube.size() > capacity) {
                System.out.println("ERROR: Tube overflow");
                return false;
            }
            for (Color c : tube) {
                if (!c.isEmpty()) {
                    counts.merge(c, 1, Integer::sum);
                }
            }
        }

        System.out.println("COLOR COUNTS:");
        for (var entry : counts.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().value()))
                .toList()) {
            System.out.println("  Color " + entry.getKey() + ": " + entry.getValue() + " drops");
            if (entry.getValue() % capacity != 0) {
                System.out.println("    WARNING: Not divisible by " + capacity);
            }
        }

        long emptyTubes = config.stream().filter(List::isEmpty).count();
        long colorCount = counts.size();

        System.out.println("Colors: " + colorCount + ", Empty tubes: " + emptyTubes);
        System.out.println("Total tubes: " + config.size());

        // Для решения нужно: цветов <= заполненных пробирок + пустых
        return true;
    }

    private static void printDiagnostics(PuzzleState state) {
        System.out.println("\nDIAGNOSTICS:");
        System.out.println("Possible moves from start: " + state.getPossibleMoves().size());
        System.out.println("Is solved? " + state.isSolved());

        // Проверим, сколько цветов в каждой пробирке
        for (int i = 0; i < state.getTubes().size(); i++) {
            Tube t = state.getTubes().get(i);
            long uniqueColors = t.getContents().stream().distinct().count();
            System.out.println("Tube " + i + ": " + uniqueColors + " unique colors");
        }
    }

    private static void verifyAndPrint(Solution sol, PuzzleState initial, PuzzleVisualizer viz) {
        PuzzleState current = initial;

        for (Move m : sol.moves()) {
            current = current.apply(m).orElse(null);
            if (current == null) {
                viz.printFailure();
                return;
            }
        }

        // Выводим финальное состояние
        System.out.println("\nFINAL STATUS:");
        viz.printState(current);

        if (current.isSolved()) {
            viz.printSuccess();
        } else {
            viz.printFailure();
        }
    }

    private static List<List<Color>> createExampleConfiguration() {
        return List.of(
                List.of(c(2), c(10), c(4), c(4)),
                List.of(c(1), c(8), c(8), c(8)),
                List.of(c(10), c(7), c(5), c(9)),
                List.of(c(5), c(3), c(2), c(5)),
                List.of(c(6), c(11), c(8), c(7)),
                List.of(c(7), c(7), c(1), c(2)),
                List.of(c(4), c(7), c(8), c(11)),
                List.of(c(10), c(11), c(3), c(1)),
                List.of(c(10), c(7), c(9), c(9)),
                List.of(c(6), c(2), c(6), c(11)),
                List.of(c(4), c(6), c(9), c(3)),
                List.of(c(5), c(3), c(3), c(1)),
                List.of(), // 12 — пустая
                List.of()  // 13 — пустая
        );
    }

    private static Color c(int v) {
        return new Color(v);
    }
}