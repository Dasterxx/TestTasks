package doczilla.com.task1.strategy;

import doczilla.com.task1.domain.Color;
import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;
import doczilla.com.task1.domain.Tube;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Deprecated
public class HeuristicStrategy implements SolvingStrategy {
    private final int maxIterations;

    public HeuristicStrategy() {
        this(100_000);
    }

    public HeuristicStrategy(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Override
    public Optional<Solution> solve(PuzzleState initialState) {
        System.out.println("Heuristic started");

        PuzzleState current = initialState;
        List<Move> path = new ArrayList<>();
        Set<PuzzleState> visited = new HashSet<>();
        visited.add(current);

        for (int i = 0; i < maxIterations; i++) {
            if (current.isSolved()) {
                System.out.println("Heuristic solved in " + i + " steps");
                return Optional.of(new Solution(path, current, i));
            }

            Move bestMove = null;
            PuzzleState bestState = null;
            double bestScore = Double.NEGATIVE_INFINITY;

            List<Move> possibleMoves = current.getPossibleMoves();

            for (Move move : possibleMoves) {
                Optional<PuzzleState> next = current.apply(move);
                if (next.isEmpty()) continue;

                PuzzleState state = next.get();
                if (visited.contains(state)) continue;

                double score = evaluate(state);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                    bestState = state;
                }
            }

            if (bestMove == null) {
                System.out.println("Heuristic stuck at step " + i + ", falling back to BFS");
                // Вызываем BFS с большим лимитом
                return new BFSStrategy(10_000_000).solve(initialState);
            }

            path.add(bestMove);
            visited.add(bestState);
            current = bestState;
        }

        System.out.println("Heuristic exceeded maxIterations");
        return Optional.empty();
    }

    private double evaluate(PuzzleState state) {
        double score = 0;
        for (Tube tube : state.getTubes()) {
            if (tube.isEmpty()) {
                score += 10;
            } else if (tube.isUniform()) {
                score += 50;
                // Bonus for full uniform tubes
                if (tube.isFull()) score += 20;
            } else {
                // Penalize mixed tubes
                long colorChanges = countColorChanges(tube);
                score -= colorChanges * 10;
            }
        }
        return score;
    }

    private long countColorChanges(Tube tube) {
        List<Color> contents = tube.getContents();
        if (contents.size() < 2) return 0;

        long changes = 0;
        for (int i = 1; i < contents.size(); i++) {
            if (!contents.get(i).equals(contents.get(i - 1))) {
                changes++;
            }
        }
        return changes;
    }
}

