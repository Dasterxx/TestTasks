package doczilla.com.task1.strategy.fastsolver;

import doczilla.com.task1.domain.Color;
import doczilla.com.task1.domain.PuzzleState;
import doczilla.com.task1.domain.Tube;

import java.util.List;

class HeuristicCalculator {

    int calculate(PuzzleState state) {
        int score = 0;

        for (Tube tube : state.getTubes()) {
            List<Color> contents = tube.getContents();
            if (contents.size() <= 1) continue;

            int misplaced = 0;
            Color target = contents.getLast();

            for (int i = contents.size() - 2; i >= 0; i--) {
                if (!contents.get(i).equals(target)) {
                    misplaced += 2;
                    target = contents.get(i);
                }
            }
            score += misplaced;
        }

        return score;
    }
}
