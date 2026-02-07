package doczilla.com.task2.fileexchange.adapters.out.audit;

import doczilla.com.task2.fileexchange.application.ports.out.AuditLogPort;
import doczilla.com.task2.fileexchange.domain.model.FileId;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class ConsoleAuditLogAdapter implements AuditLogPort {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_INSTANT;

    @Override
    public void log(String action, FileId targetId, UserId userId) {
        String timestamp = FORMATTER.format(Instant.now());
        String user = userId != null ? userId.toString() : "ANONYMOUS";

        System.out.printf("[%s] AUDIT: action=%s, file=%s, user=%s%n",
                timestamp, action, targetId.toFullString(), user);
    }

    @Override
    public void log(String action, FileId targetId, UserId userId, String details) {
        log(action, targetId, userId);
        System.out.printf("  details: %s%n", details);
    }
}
