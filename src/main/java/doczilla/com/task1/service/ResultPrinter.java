package doczilla.com.task1.service;

import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Solution;
import doczilla.com.task1.vizualizer.PuzzleVisualizer;

public class ResultPrinter {
    private final PuzzleVisualizer visualizer;
    private final SolutionVerifier verifier;

    public ResultPrinter(PuzzleVisualizer visualizer) {
        this.visualizer = visualizer;
        this.verifier = new SolutionVerifier();
    }

    public void print(Solution solution, PuzzleState initialState, long timeMs) {
        System.out.println("TIME: " + timeMs + " MS");
        System.out.println("SOLVED in " + solution.moves().size() + " moves");
        System.out.println("FINAL STATE:");
        visualizer.printState(solution.finalState());

        boolean correct = verifier.verify(solution, initialState);
        System.out.println(correct ? "[OK]" : "[FAIL]");
    }

    public void printFailure() {
        System.out.println("NO SOLUTION");
    }
}
