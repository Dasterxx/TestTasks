package doczilla.com.task1.strategy;

import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class BFSStrategy implements SolvingStrategy {
    private final int maxIterations;

    public BFSStrategy() {
        this(50_000_000);
    }

    public BFSStrategy(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Override
    public Optional<Solution> solve(PuzzleState initialState) {
        System.out.println("BFS starting with maxIterations: " + maxIterations);

        if (initialState.isSolved()) {
            return Optional.of(new Solution(List.of(), initialState, 0));
        }

        record StateNode(PuzzleState state, List<Move> path) {}

        Queue<StateNode> queue = new ArrayDeque<>();
        Set<PuzzleState> visited = new HashSet<>();

        queue.offer(new StateNode(initialState, List.of()));
        visited.add(initialState);

        int explored = 0;
        int lastPrint = 0;

        while (!queue.isEmpty() && explored < maxIterations) {
            StateNode current = queue.poll();
            explored++;

            // Прогресс каждые 100000 итераций
            if (explored - lastPrint >= 100_000) {
                System.out.println("BFS progress: " + explored + " states, queue: " + queue.size());
                lastPrint = explored;
            }

            for (Move move : current.state().getPossibleMoves()) {
                Optional<PuzzleState> nextState = current.state().apply(move);
                if (nextState.isEmpty()) continue;

                PuzzleState next = nextState.get();
                if (visited.contains(next)) continue;

                List<Move> newPath = new ArrayList<>(current.path());
                newPath.add(move);

                if (next.isSolved()) {
                    System.out.println("BFS solved! Explored: " + explored);
                    return Optional.of(new Solution(newPath, next, explored));
                }

                visited.add(next);
                queue.offer(new StateNode(next, newPath));
            }
        }

        System.out.println("BFS failed. Explored: " + explored + ", queue empty: " + queue.isEmpty());
        return Optional.empty();
    }
}