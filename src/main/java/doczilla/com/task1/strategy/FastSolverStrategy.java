package doczilla.com.task1.strategy;

import doczilla.com.task1.domain.*;

import java.util.*;

public class FastSolverStrategy implements SolvingStrategy {
    private static final int TIME_LIMIT_MS = 60000;
    private static final int MAX_SOLUTION_LENGTH = 200;

    private long startTime;
    private int nodesExplored;

    @Override
    public Optional<Solution> solve(PuzzleState initialState) {
        System.out.println("A* Solver starting...");

        if (initialState.isSolved()) {
            return Optional.of(new Solution(List.of(), initialState, 0));
        }

        startTime = System.currentTimeMillis();

        // A* с приоритетной очередью
        System.out.println("Trying A* search...");
        List<Move> result = aStar(initialState);

        if (result != null) {
            long time = System.currentTimeMillis() - startTime;
            System.out.println("SOLVED! Depth: " + result.size() + ", nodes: " + nodesExplored + ", time: " + time + "ms");
            return Optional.of(new Solution(result, applyMoves(initialState, result), nodesExplored));
        }

        return Optional.empty();
    }

    private List<Move> aStar(PuzzleState initial) {
        record Node(PuzzleState state, List<Move> path, int fScore) {}

        // Приоритетная очередь по f = g + h (путь + эвристика)
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::fScore));
        Set<PuzzleState> closedSet = new HashSet<>();
        Map<PuzzleState, Integer> gScore = new HashMap<>();

        openSet.offer(new Node(initial, List.of(), heuristic(initial)));
        gScore.put(initial, 0);

        while (!openSet.isEmpty()) {
            if (System.currentTimeMillis() - startTime > TIME_LIMIT_MS) {
                return null;
            }

            Node current = openSet.poll();
            nodesExplored++;

            if (current.state().isSolved()) {
                return current.path();
            }

            if (closedSet.contains(current.state())) {
                continue;
            }

            closedSet.add(current.state());

            // Ограничение глубины
            if (current.path().size() >= MAX_SOLUTION_LENGTH) {
                continue;
            }

            for (Move move : current.state().getPossibleMoves()) {
                Optional<PuzzleState> nextOpt = current.state().apply(move);
                if (nextOpt.isEmpty()) continue;

                PuzzleState next = nextOpt.get();

                if (closedSet.contains(next)) {
                    continue;
                }

                int tentativeG = current.path().size() + 1;
                Integer existingG = gScore.get(next);

                if (existingG != null && tentativeG >= existingG) {
                    continue;
                }

                gScore.put(next, tentativeG);
                int f = tentativeG + heuristic(next);

                List<Move> newPath = new ArrayList<>(current.path());
                newPath.add(move);

                openSet.offer(new Node(next, newPath, f));
            }

            if (nodesExplored % 100000 == 0) {
                System.out.println("  Nodes: " + nodesExplored/1000 + "k, queue: " + openSet.size() + ", f-best: " + (openSet.isEmpty() ? "?" : openSet.peek().fScore()));
            }
        }

        return null;
    }

    // Улучшенная эвристика
    private int heuristic(PuzzleState state) {
        int score = 0;

        for (Tube tube : state.getTubes()) {
            List<Color> contents = tube.getContents();
            if (contents.size() <= 1) continue;

            // Считаем "нарушения" - сколько капель не на своём месте
            int misplaced = 0;
            Color target = contents.get(contents.size() - 1);

            for (int i = contents.size() - 2; i >= 0; i--) {
                if (!contents.get(i).equals(target)) {
                    misplaced += 2; // Нужно вынуть и положить
                    target = contents.get(i);
                }
            }
            score += misplaced;
        }

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