package doczilla.com.task1;

import doczilla.com.task1.domain.*;
import doczilla.com.task1.strategy.FastSolverStrategy;
import doczilla.com.task1.strategy.QuickSolverStrategy;
import doczilla.com.task1.util.ValidConf;
import doczilla.com.task1.vizualizer.PuzzleVisualizer;

import java.util.List;
import java.util.Optional;

public class WaterSortApplication {
    private static final ValidConf validConfig = new ValidConf();

    public static void main(String[] args) {
        int capacity = 4;
        int colors = 12;
        int emptyTubes = 2;

        //  ВАЛИДНАЯ конфигурация
        List<List<Color>> config = validConfig.trap();
        PuzzleState initialState = PuzzleState.create(config, capacity);

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
            solution = new FastSolverStrategy().solve(initialState);
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