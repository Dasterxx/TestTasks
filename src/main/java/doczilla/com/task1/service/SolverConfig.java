package doczilla.com.task1.service;

public record SolverConfig(boolean useQuickSolver, boolean useFastSolver, int quickSolverMaxMoves) {
    public static SolverConfig defaultConfig() {
        return new SolverConfig(true, true, 300);
    }

    public static SolverConfig fastOnly() {
        return new SolverConfig(false, true, 0);
    }

    public static SolverConfig quickOnly() {
        return new SolverConfig(true, false, 300);
    }
}
