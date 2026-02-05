package doczilla.com.task1;

import doczilla.com.task1.colortasks.AlmostFullColors;
import doczilla.com.task1.domain.*;
import doczilla.com.task1.strategy.IDAStarStrategy;
import doczilla.com.task1.strategy.QuickSolverStrategy;
import doczilla.com.task1.util.ValidConf;
import doczilla.com.task1.vizualizer.PuzzleVisualizer;

import java.util.List;
import java.util.Optional;

import static doczilla.com.task1.colortasks.AlmostFullColors.createSimpleConfiguration;
import static doczilla.com.task1.colortasks.HardConfiguration.createHardConfiguration;

public class WaterSortApplication {
    private static final ValidConf validConfig = new ValidConf();
    private static final AlmostFullColors almostFullColors = new AlmostFullColors();

    public static void main(String[] args) {
        int capacity = 4;

        //  ВАЛИДНАЯ конфигурация
//        List<List<Color>> config = validConfig.generateValidConfiguration(colors, emptyTubes, capacity);
//        PuzzleState initialState = PuzzleState.create(config, capacity);

        // тестовая конфигурация для проверки:
//        List<List<Color>> config = createSimpleConfiguration();
//        PuzzleState initialState = PuzzleState.create(config, capacity);

        //hard config
        // очень долго решается
        List<List<Color>> hardConfig = createHardConfiguration();
        PuzzleState initialState = PuzzleState.create(hardConfig, capacity);

        if (initialState.isSolved()) {
            System.out.println("ALREADY SOLVED");
            return;
        }

        var visualizer = new PuzzleVisualizer();
        System.out.println("START:");
        visualizer.printState(initialState);

        long start = System.currentTimeMillis();
        Optional<Solution> solution;

        // Пробуем быстрый солвер первым
        System.out.println("Trying QuickSolver...");
        solution = new QuickSolverStrategy(300).solve(initialState);

        // Если не сработал — IDA*
        if (solution.isEmpty()) {
            System.out.println("QuickSolver failed, trying IDA*...");
            solution = new IDAStarStrategy(100).solve(initialState);
        }

        long time = System.currentTimeMillis() - start;

        System.out.println("TIME: " + time + " MS");

        solution.ifPresentOrElse(
                sol -> {
                    System.out.println("SOLVED in " + sol.moves().size() + " moves");
                    System.out.println("FINAL STATE:");
                    visualizer.printState(sol.finalState());

                    // Проверка
                    boolean correct = verifySolution(sol, initialState);
                    System.out.println(correct ? "[OK]" : "[FAIL]");
                },
                () -> System.out.println("NO SOLUTION")
        );
    }

    private static boolean verifySolution(Solution sol, PuzzleState initial) {
        PuzzleState current = initial;
        for (Move m : sol.moves()) {
            current = current.apply(m).orElse(null);
            if (current == null) return false;
        }
        return current.isSolved();
    }

}