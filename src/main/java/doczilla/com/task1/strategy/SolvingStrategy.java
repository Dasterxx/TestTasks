package doczilla.com.task1.strategy;

import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;

import java.util.Optional;

public interface SolvingStrategy {
    Optional<Solution> solve(PuzzleState initialState);
}
