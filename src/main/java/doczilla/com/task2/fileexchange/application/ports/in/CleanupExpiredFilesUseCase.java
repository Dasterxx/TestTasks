package doczilla.com.task2.fileexchange.application.ports.in;

public interface CleanupExpiredFilesUseCase {

    CleanupResult cleanup();

    record CleanupResult(int deletedCount, long freedBytes) {}
}
