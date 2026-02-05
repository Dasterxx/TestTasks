package doczilla.com.task1.service;

import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;
import doczilla.com.task1.strategy.fastsolver.FastSolverStrategy;
import doczilla.com.task1.strategy.quicksolver.QuickSolverStrategy;

import java.util.Optional;

public class SolverService {
    private final SolverConfig config;

    public SolverService() {
        this(SolverConfig.defaultConfig());
    }

    public SolverService(SolverConfig config) {
        this.config = config;
    }

    public Optional<Solution> solve(PuzzleState initialState) {
        Optional<Solution> solution = Optional.empty();

        if (config.useQuickSolver()) {
            System.out.println("Trying QuickSolver...");
            solution = new QuickSolverStrategy(config.quickSolverMaxMoves()).solve(initialState);
        }

        if (solution.isEmpty() && config.useFastSolver()) {
            System.out.println(config.useQuickSolver() ? "QuickSolver failed, trying A*..." : "Trying A*...");
            solution = new FastSolverStrategy().solve(initialState);
        }

        return solution;
    }
}