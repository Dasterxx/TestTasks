package doczilla.com.task1.service;

import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;
import doczilla.com.task1.strategy.BFSStrategy;
import doczilla.com.task1.strategy.HeuristicStrategy;
import doczilla.com.task1.strategy.SolvingStrategy;

import java.util.Objects;
import java.util.Optional;

public class PuzzleSolver {
    private final SolvingStrategy strategy;

    public PuzzleSolver(SolvingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public Optional<Solution> solve(PuzzleState initialState) {
        return strategy.solve(initialState);
    }

    public static PuzzleSolver bfs() {
        return new PuzzleSolver(new BFSStrategy());
    }

    public static PuzzleSolver heuristic() {
        return new PuzzleSolver(new HeuristicStrategy());
    }
}
