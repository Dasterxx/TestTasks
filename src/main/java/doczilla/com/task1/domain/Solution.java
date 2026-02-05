package doczilla.com.task1.domain;

import java.util.List;

public record Solution(List<Move> moves, PuzzleState finalState, int stepsExplored) {
    public Solution {
        moves = List.copyOf(moves);
    }
}
