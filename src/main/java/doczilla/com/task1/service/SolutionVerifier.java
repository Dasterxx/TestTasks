package doczilla.com.task1.service;

import doczilla.com.task1.domain.Move;
import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;

public class SolutionVerifier {

    public boolean verify(Solution solution, PuzzleState initialState) {
        PuzzleState current = initialState;
        for (Move m : solution.moves()) {
            current = current.apply(m).orElse(null);
            if (current == null) return false;
        }
        return current.isSolved();
    }
}
