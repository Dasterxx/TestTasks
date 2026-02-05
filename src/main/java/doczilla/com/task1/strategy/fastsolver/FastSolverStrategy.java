package doczilla.com.task1.strategy.fastsolver;

import doczilla.com.task1.domain.*;
import doczilla.com.task1.strategy.SolvingStrategy;

import java.util.List;
import java.util.Optional;

public class FastSolverStrategy implements SolvingStrategy {

    @Override
    public Optional<Solution> solve(PuzzleState initialState) {
        System.out.println("A* Solver starting...");

        if (initialState.isSolved()) {
            return Optional.of(new Solution(List.of(), initialState, 0));
        }

        long startTime = System.currentTimeMillis();
        System.out.println("Trying A* search...");

        AStarSearch search = new AStarSearch();
        List<Move> result = search.execute(initialState, startTime);

        if (result != null) {
            long time = System.currentTimeMillis() - startTime;
            System.out.println("SOLVED! Depth: " + result.size() + ", nodes: " +
                    search.getNodesExplored() + ", time: " + time + "ms");
            PuzzleState finalState = new StateApplier().apply(initialState, result);
            return Optional.of(new Solution(result, finalState, search.getNodesExplored()));
        }

        return Optional.empty();
    }
}