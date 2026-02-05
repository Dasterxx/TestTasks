package doczilla.com.task1.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PuzzleState {
    private final List<Tube> tubes;
    private final int capacity;

    public PuzzleState(List<Tube> tubes, int capacity) {
        this.tubes = List.copyOf(tubes);
        this.capacity = capacity;
    }

    public static PuzzleState create(List<List<Color>> initialConfiguration, int capacity) {
        List<Tube> tubes = initialConfiguration.stream()
                .map(contents -> Tube.of(contents, capacity))
                .collect(Collectors.toList());
        return new PuzzleState(tubes, capacity);
    }

    public boolean isSolved() {
        return tubes.stream().allMatch(Tube::isUniform);
    }

    public Optional<PuzzleState> apply(Move move) {
        if (move.fromTube() >= tubes.size() || move.toTube() >= tubes.size()) {
            return Optional.empty();
        }

        Tube source = tubes.get(move.fromTube());
        Tube target = tubes.get(move.toTube());

        Optional<ColorSegment> segmentOpt = source.getTopSegment();
        if (segmentOpt.isEmpty()) return Optional.empty();

        ColorSegment segment = segmentOpt.get();

        // Try to pour into target
        Optional<Tube> newTarget = target.pourIn(segment);
        if (newTarget.isEmpty()) return Optional.empty();

        // Calculate how much was actually poured
        int poured = Math.min(segment.count(), target.getEmptySpace());
        Optional<Tube> newSource = source.pourOut(poured);

        // Build new state
        List<Tube> newTubes = new ArrayList<>(tubes);
        newTubes.set(move.fromTube(), newSource.orElseThrow());
        newTubes.set(move.toTube(), newTarget.orElseThrow());

        return Optional.of(new PuzzleState(newTubes, capacity));
    }

    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < tubes.size(); i++) {
            for (int j = 0; j < tubes.size(); j++) {
                if (i == j) continue;
                Move move = new Move(i, j);
                if (apply(move).isPresent()) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public List<Tube> getTubes() {
        return tubes;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PuzzleState that)) return false;
        return capacity == that.capacity && tubes.equals(that.tubes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tubes, capacity);
    }
}