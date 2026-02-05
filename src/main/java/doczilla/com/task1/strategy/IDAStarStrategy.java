package doczilla.com.task1.strategy;

import doczilla.com.task1.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class IDAStarStrategy implements SolvingStrategy {
    private final int maxDepth;
    private int nodesExplored;

    public IDAStarStrategy() {
        this(100);
    }

    public IDAStarStrategy(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public Optional<Solution> solve(PuzzleState initialState) {
        System.out.println("IDA* starting...");
        nodesExplored = 0;

        // Начинаем с эвристики начального состояния
        int threshold = heuristic(initialState);

        for (int depth = 0; depth <= maxDepth; depth++) {
            System.out.println("Trying threshold: " + threshold);
            List<Move> path = new ArrayList<>();
            int result = search(initialState, 0, threshold, path, new HashSet<>());

            if (result == Integer.MAX_VALUE) {
                // Найдено решение
                return Optional.of(new Solution(path, initialState, nodesExplored));
            }
            if (result == Integer.MIN_VALUE) {
                // Нет решения
                return Optional.empty();
            }

            threshold = result; // Увеличиваем порог
        }

        return Optional.empty();
    }

    // Возвращает: MAX_VALUE = решение найдено, MIN_VALUE = тупик, иначе новый порог
    private int search(PuzzleState state, int g, int threshold, List<Move> path, Set<PuzzleState> visited) {
        int f = g + heuristic(state);
        if (f > threshold) return f;
        if (state.isSolved()) return Integer.MAX_VALUE;

        int min = Integer.MAX_VALUE;
        visited.add(state);
        nodesExplored++;

        if (nodesExplored % 100000 == 0) {
            System.out.println("IDA* nodes: " + nodesExplored + ", depth: " + g);
        }

        for (Move move : state.getPossibleMoves()) {
            Optional<PuzzleState> nextOpt = state.apply(move);
            if (nextOpt.isEmpty()) continue;

            PuzzleState next = nextOpt.get();
            if (visited.contains(next)) continue;

            path.add(move);
            int t = search(next, g + 1, threshold, path, visited);

            if (t == Integer.MAX_VALUE) return Integer.MAX_VALUE;
            if (t < min) min = t;

            path.remove(path.size() - 1); // Backtrack
        }

        visited.remove(state);
        return min == Integer.MAX_VALUE ? Integer.MIN_VALUE : min;
    }

    // Улучшенная эвристика
    private int heuristic(PuzzleState state) {
        int score = 0;

        for (Tube tube : state.getTubes()) {
            if (tube.isEmpty()) continue;

            List<Color> contents = tube.getContents();
            if (contents.isEmpty()) continue;

            // Штраф за каждую "лишнюю" каплю не того цвета
            Color target = contents.get(contents.size() - 1); // Верхний цвет
            int wrongColors = 0;
            int correctColors = 0;

            for (int i = contents.size() - 1; i >= 0; i--) {
                if (contents.get(i).equals(target)) {
                    correctColors++;
                } else {
                    wrongColors++;
                    target = contents.get(i); // Меняем целевой цвет
                }
            }

            // Чем больше групп цветов, тем хуже
            score += wrongColors * 2;

            // Штраф за неполные правильные группы
            if (correctColors > 0 && correctColors < 4) {
                score += (4 - correctColors);
            }
        }

        return score;
    }
}
