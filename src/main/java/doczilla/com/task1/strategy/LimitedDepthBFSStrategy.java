package doczilla.com.task1.strategy;

import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LimitedDepthBFSStrategy implements SolvingStrategy {
    private final int maxDepth;

    public LimitedDepthBFSStrategy(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public Optional<Solution> solve(PuzzleState initialState) {
        for (int depth = 1; depth <= maxDepth; depth++) {
            System.out.println("Trying depth " + depth);
            Optional<Solution> result = depthLimitedSearch(initialState, depth, new HashSet<>(), 0);
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    }

    private Optional<Solution> depthLimitedSearch(PuzzleState state, int limit,
                                                  Set<PuzzleState> visited, int explored) {
        if (state.isSolved()) {
            return Optional.of(new Solution(List.of(), state, explored));
        }
        if (limit == 0) return Optional.empty();

        visited.add(state);

        for (Move move : state.getPossibleMoves()) {
            Optional<PuzzleState> next = state.apply(move);
            if (next.isEmpty() || visited.contains(next.get())) continue;

            Optional<Solution> result = depthLimitedSearch(next.get(), limit - 1, visited, explored + 1);
            if (result.isPresent()) {
                List<Move> moves = new ArrayList<>();
                moves.add(move);
                moves.addAll(result.get().moves());
                return Optional.of(new Solution(moves, result.get().finalState(), explored));
            }
        }

        visited.remove(state);
        return Optional.empty();
    }
}
