package doczilla.com.task1;

import doczilla.com.task1.domain.*;
import doczilla.com.task1.service.ResultPrinter;
import doczilla.com.task1.service.SolverService;
import doczilla.com.task1.service.SolverServiceFactory;
import doczilla.com.task1.util.DifferentStrategies;
import doczilla.com.task1.vizualizer.PuzzleVisualizer;

import java.util.List;
import java.util.Optional;

public class WaterSortApplication {
    private static final DifferentStrategies CONFIG = new DifferentStrategies();

    public static void main(String[] args) {
        int capacity = 4;

        List<List<Color>> config = CONFIG.test1();
        PuzzleState initialState = PuzzleState.create(config, capacity);

        if (initialState.isSolved()) {
            System.out.println("ALREADY SOLVED");
            return;
        }

        var visualizer = new PuzzleVisualizer();
        System.out.println("START:");
        visualizer.printState(initialState);

        long start = System.currentTimeMillis();

        // Выбор конфигурации:
         Optional<Solution> solution = SolverServiceFactory.defaultService().solve(initialState); // Оба
        //Optional<Solution> solution = SolverServiceFactory.fastOnly().solve(initialState); // Только A*
        //Optional<Solution> solution = SolverServiceFactory.quickOnly().solve(initialState); // Только Quick

        long time = System.currentTimeMillis() - start;

        solution.ifPresentOrElse(
                sol -> new ResultPrinter(visualizer).print(sol, initialState, time),
                () -> System.out.println("NO SOLUTION")
        );
    }
}