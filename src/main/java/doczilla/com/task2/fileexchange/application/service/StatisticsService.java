package doczilla.com.task2.fileexchange.application.service;

import doczilla.com.task2.fileexchange.application.ports.in.GetStatisticsUseCase;
import doczilla.com.task2.fileexchange.domain.repository.FileIndexPort;

import java.util.Objects;

public class StatisticsService implements GetStatisticsUseCase {

    private final FileIndexPort fileIndexPort;
    private long totalDownloads = 0;

    public StatisticsService(FileIndexPort fileIndexPort) {
        this.fileIndexPort = Objects.requireNonNull(fileIndexPort);
    }

    @Override
    public Statistics getStatistics() {
        return new Statistics(
                fileIndexPort.count(),
                totalDownloads,
                fileIndexPort.totalSize()
        );
    }

    public void incrementDownloads() {
        totalDownloads++;
    }
}
