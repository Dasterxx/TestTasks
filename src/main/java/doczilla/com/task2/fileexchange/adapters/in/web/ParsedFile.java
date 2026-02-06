package doczilla.com.task2.fileexchange.adapters.in.web;


public record ParsedFile(
        String fileName,
        String contentType,
        byte[] content
) {
    public ParsedFile {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Filename required");
        }
        if (content == null) {
            content = new byte[0];
        }
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }
    }
}