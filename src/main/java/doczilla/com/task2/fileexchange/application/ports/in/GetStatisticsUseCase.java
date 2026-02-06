package doczilla.com.task2.fileexchange.application.ports.in;

public interface GetStatisticsUseCase {

    Statistics getStatistics();

    record Statistics(
            long totalFiles,
            long totalDownloads,
            long totalBytesUploaded
    ) {}
}
