package doczilla.com.task1.strategy.fastsolver;

import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;

import java.util.List;

class StateApplier {

    PuzzleState apply(PuzzleState initial, List<Move> moves) {
        PuzzleState current = initial;
        for (Move m : moves) {
            current = current.apply(m).orElseThrow();
        }
        return current;
    }
}
