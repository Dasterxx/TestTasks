package doczilla.com.task1.strategy;

import doczilla.com.task1.domain.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;


public class QuickSolverStrategy implements SolvingStrategy {
    private final int maxMoves;
    private final Random random;

    public QuickSolverStrategy() {
        this(200);
    }

    public QuickSolverStrategy(int maxMoves) {
        this.maxMoves = maxMoves;
        this.random = new Random();
    }

    @Override
    public Optional<Solution> solve(PuzzleState initialState) {
        System.out.println("Quick solver starting...");

        // Пробуем несколько раз с разными случайными выборами
        for (int attempt = 0; attempt < 100; attempt++) {
            List<Move> path = trySolve(initialState);
            if (path != null) {
                System.out.println("Solved in attempt " + attempt + " with " + path.size() + " moves");
                PuzzleState finalState = applyMoves(initialState, path);
                return Optional.of(new Solution(path, finalState, attempt * maxMoves));
            }
        }

        return Optional.empty();
    }

    private List<Move> trySolve(PuzzleState initial) {
        PuzzleState current = initial;
        List<Move> path = new ArrayList<>();
        Set<PuzzleState> visited = new HashSet<>();
        visited.add(current);

        for (int i = 0; i < maxMoves; i++) {
            if (current.isSolved()) return path;

            List<Move> moves = current.getPossibleMoves();

            // Убираем ходы, которые ведут в посещённые состояния
            PuzzleState finalCurrent1 = current;
            List<Move> goodMoves = moves.stream()
                    .filter(m -> {
                        PuzzleState next = finalCurrent1.apply(m).orElse(null);
                        return next != null && !visited.contains(next);
                    })
                    .toList();

            if (goodMoves.isEmpty()) {
                // Откат на 5 ходов
                if (path.size() < 5) return null;
                for (int j = 0; j < 5 && !path.isEmpty(); j++) {
                    path.remove(path.size() - 1);
                }
                current = applyMoves(initial, path);
                visited.clear();
                visited.add(current);
                continue;
            }

            // Выбираем лучший ход по эвристике
            PuzzleState finalCurrent = current;
            Move best = goodMoves.stream()
                    .max(Comparator.comparingInt(m -> scoreMove(finalCurrent, m)))
                    .orElse(goodMoves.get(random.nextInt(goodMoves.size())));

            current = current.apply(best).orElseThrow();
            path.add(best);
            visited.add(current);
        }

        return null;
    }

    private int scoreMove(PuzzleState state, Move move) {
        PuzzleState next = state.apply(move).orElseThrow();

        int score = 0;
        for (Tube tube : next.getTubes()) {
            if (tube.isUniform()) score += 10;
            if (tube.isFull() && tube.isUniform()) score += 50;
        }

        // Бонус за заполнение пробирки
        Tube target = next.getTubes().get(move.toTube());
        if (target.isFull() && target.isUniform()) score += 100;

        return score;
    }

    private PuzzleState applyMoves(PuzzleState initial, List<Move> moves) {
        PuzzleState current = initial;
        for (Move m : moves) {
            current = current.apply(m).orElseThrow();
        }
        return current;
    }
}
