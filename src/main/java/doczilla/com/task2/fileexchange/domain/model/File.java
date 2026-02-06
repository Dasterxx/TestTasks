package doczilla.com.task2.fileexchange.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class File {
    private final FileId id;
    private final FileName name;
    private final ContentType contentType;
    private final long sizeBytes;
    private final UserId ownerId;
    private final Instant uploadedAt;
    private Instant lastAccessedAt;
    private int downloadCount;
    private boolean deleted;

    private File(FileId id, FileName name, ContentType contentType,
                 long sizeBytes, UserId ownerId, Instant uploadedAt) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.contentType = Objects.requireNonNull(contentType);
        this.sizeBytes = validateSize(sizeBytes);
        this.ownerId = ownerId;
        this.uploadedAt = Objects.requireNonNull(uploadedAt);
        this.lastAccessedAt = uploadedAt;
        this.downloadCount = 0;
        this.deleted = false;
    }

    public static File create(FileName name, ContentType contentType,
                              long sizeBytes, UserId ownerId) {
        return new File(
                FileId.generate(),
                name,
                contentType,
                sizeBytes,
                ownerId,
                Instant.now()
        );
    }

    public static File reconstruct(FileId id, FileName name,
                                   ContentType contentType, long sizeBytes,
                                   UserId ownerId, Instant uploadedAt,
                                   Instant lastAccessedAt, int downloadCount) {
        File file = new File(id, name, contentType, sizeBytes, ownerId, uploadedAt);
        file.lastAccessedAt = lastAccessedAt;
        file.downloadCount = downloadCount;
        return file;
    }

    public void recordDownload() {
        if (deleted) {
            throw new IllegalStateException("Cannot download deleted file");
        }
        this.lastAccessedAt = Instant.now();
        this.downloadCount++;
    }

    public boolean isAccessibleBy(UserId user) {
        if (deleted) return false;
        if (ownerId == null) return true; // Публичный файл
        return ownerId.equals(user);
    }

    public boolean isExpired(int daysThreshold) {
        Instant expiration = lastAccessedAt.plusSeconds(daysThreshold * 86400L);
        return Instant.now().isAfter(expiration);
    }

    public void markDeleted() {
        this.deleted = true;
    }

    private long validateSize(long size) {
        if (size <= 0) throw new IllegalArgumentException("Size must be positive");
        if (size > 10L * 1024 * 1024 * 1024) { // 10GB max
            throw new IllegalArgumentException("File too large");
        }
        return size;
    }

    // Getters - только read-only доступ
    public FileId getId() { return id; }
    public FileName getName() { return name; }
    public ContentType getContentType() { return contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public UserId getOwnerId() { return ownerId; }
    public Instant getUploadedAt() { return uploadedAt; }
    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public int getDownloadCount() { return downloadCount; }
    public boolean isDeleted() { return deleted; }
}

