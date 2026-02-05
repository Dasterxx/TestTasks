package doczilla.com.task1.strategy.fastsolver;

import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

import static java.util.Comparator.comparingInt;

class AStarSearch {
    private static final int TIME_LIMIT_MS = 60000;
    private static final int MAX_SOLUTION_LENGTH = 200;

    private final HeuristicCalculator heuristic = new HeuristicCalculator();
    private long startTime;
    private int nodesExplored;

    List<Move> execute(PuzzleState initial, long startTime) {
        this.startTime = startTime;
        this.nodesExplored = 0;

        PriorityQueue<Node> openSet = new PriorityQueue<>(comparingInt(Node::fScore));
        Set<PuzzleState> closedSet = new HashSet<>();
        Map<PuzzleState, Integer> gScore = new HashMap<>();

        openSet.offer(new Node(initial, List.of(), heuristic.calculate(initial)));
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

            if (closedSet.contains(current.state())) continue;
            closedSet.add(current.state());

            if (current.path().size() >= MAX_SOLUTION_LENGTH) continue;

            processNeighbors(current, openSet, closedSet, gScore);

            if (nodesExplored % 100000 == 0) {
                printProgress(openSet);
            }
        }

        return null;
    }

    private void processNeighbors(Node current, PriorityQueue<Node> openSet,
                                  Set<PuzzleState> closedSet, Map<PuzzleState, Integer> gScore) {
        for (Move move : current.state().getPossibleMoves()) {
            Optional<PuzzleState> nextOpt = current.state().apply(move);
            if (nextOpt.isEmpty()) continue;

            PuzzleState next = nextOpt.get();
            if (closedSet.contains(next)) continue;

            int tentativeG = current.path().size() + 1;
            Integer existingG = gScore.get(next);

            if (existingG != null && tentativeG >= existingG) continue;

            gScore.put(next, tentativeG);
            int f = tentativeG + heuristic.calculate(next);

            List<Move> newPath = new ArrayList<>(current.path());
            newPath.add(move);

            openSet.offer(new Node(next, newPath, f));
        }
    }

    private void printProgress(PriorityQueue<Node> openSet) {
        System.out.println("  Nodes: " + nodesExplored/1000 + "k, queue: " + openSet.size() +
                ", f-best: " + (openSet.isEmpty() ? "?" : openSet.peek().fScore()));
    }

    int getNodesExplored() {
        return nodesExplored;
    }
}
