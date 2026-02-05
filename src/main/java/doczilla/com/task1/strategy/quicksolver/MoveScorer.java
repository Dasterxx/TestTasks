package doczilla.com.task1.strategy.quicksolver;

import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Tube;

class MoveScorer {

    int score(PuzzleState state, Move move) {
        PuzzleState next = state.apply(move).orElseThrow();

        int score = 0;
        for (Tube tube : next.getTubes()) {
            if (tube.isUniform()) score += 10;
            if (tube.isFull() && tube.isUniform()) score += 50;
        }

        Tube target = next.getTubes().get(move.toTube());
        if (target.isFull() && target.isUniform()) score += 100;

        return score;
    }
}
