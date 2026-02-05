package doczilla.com.task1.strategy.fastsolver;

import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Move;

import java.util.List;

record Node(PuzzleState state, List<Move> path, int fScore) {}
