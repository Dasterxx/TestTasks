package doczilla.com.task1.service;

public class SolverServiceFactory {

    public static SolverService defaultService() {
        return new SolverService(SolverConfig.defaultConfig());
    }

    public static SolverService fastOnly() {
        return new SolverService(SolverConfig.fastOnly());
    }

    public static SolverService quickOnly() {
        return new SolverService(SolverConfig.quickOnly());
    }
}