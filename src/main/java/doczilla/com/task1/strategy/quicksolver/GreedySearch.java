package doczilla.com.task1.strategy.quicksolver;

import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


class GreedySearch {
    private final int maxMoves;
    private final Random random;
    private final MoveScorer scorer = new MoveScorer();
    private final StateApplier applier = new StateApplier();

    GreedySearch(int maxMoves) {
        this.maxMoves = maxMoves;
        this.random = new Random();
    }

    List<Move> execute(PuzzleState initial) {
        PuzzleState current = initial;
        List<Move> path = new ArrayList<>();
        Set<PuzzleState> visited = new HashSet<>();
        visited.add(current);

        for (int i = 0; i < maxMoves; i++) {
            if (current.isSolved()) return path;

            List<Move> goodMoves = filterGoodMoves(current, visited);

            if (goodMoves.isEmpty()) {
                if (path.size() < 5) return null;
                backtrack(path, 5);
                current = applier.apply(initial, path);
                visited.clear();
                visited.add(current);
                continue;
            }

            Move best = selectBestMove(current, goodMoves);
            current = current.apply(best).orElseThrow();
            path.add(best);
            visited.add(current);
        }

        return null;
    }

    private List<Move> filterGoodMoves(PuzzleState current, Set<PuzzleState> visited) {
        return current.getPossibleMoves().stream()
                .filter(m -> {
                    PuzzleState next = current.apply(m).orElse(null);
                    return next != null && !visited.contains(next);
                })
                .toList();
    }

    private void backtrack(List<Move> path, int steps) {
        for (int j = 0; j < steps && !path.isEmpty(); j++) {
            path.remove(path.size() - 1);
        }
    }

    private Move selectBestMove(PuzzleState current, List<Move> goodMoves) {
        return goodMoves.stream()
                .max(Comparator.comparingInt(m -> scorer.score(current, m)))
                .orElse(goodMoves.get(random.nextInt(goodMoves.size())));
    }
}
