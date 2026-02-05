package doczilla.com.task1.strategy.quicksolver;

import doczilla.com.task1.domain.*;
import doczilla.com.task1.strategy.SolvingStrategy;

import java.util.List;
import java.util.Optional;

public class QuickSolverStrategy implements SolvingStrategy {
    private final int maxMoves;

    public QuickSolverStrategy() {
        this(200);
    }

    public QuickSolverStrategy(int maxMoves) {
        this.maxMoves = maxMoves;
    }

    @Override
    public Optional<Solution> solve(PuzzleState initialState) {
        System.out.println("Quick solver starting...");

        GreedySearch search = new GreedySearch(maxMoves);

        for (int attempt = 0; attempt < 100; attempt++) {
            List<Move> path = search.execute(initialState);
            if (path != null) {
                System.out.println("Solved in attempt " + attempt + " with " + path.size() + " moves");
                PuzzleState finalState = new StateApplier().apply(initialState, path);
                return Optional.of(new Solution(path, finalState, attempt * maxMoves));
            }
        }

        return Optional.empty();
    }
}