package doczilla.com.task2.fileexchange.adapters.in.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MultipartParser {

    private static final int BUFFER_SIZE = 8192;
    private static final byte[] CRLF = "\r\n".getBytes(StandardCharsets.UTF_8);

    public ParsedFile parse(InputStream inputStream) throws IOException {
        // Читаем весь запрос в память (для больших файлов нужно streaming)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, read);
        }
        byte[] requestData = baos.toByteArray();

        // Находим boundary
        String requestStr = new String(requestData, StandardCharsets.UTF_8);
        int boundaryIdx = requestStr.indexOf("boundary=");
        if (boundaryIdx == -1) {
            throw new IllegalArgumentException("No boundary found");
        }

        String boundary = requestStr.substring(boundaryIdx + 9);
        int endIdx = boundary.indexOf("\r\n");
        if (endIdx != -1) {
            boundary = boundary.substring(0, endIdx);
        }

        String delimiter = "--" + boundary;
        byte[] delimiterBytes = delimiter.getBytes(StandardCharsets.UTF_8);

        // Ищем начало файла
        int fileStart = indexOf(requestData, delimiterBytes, 0);
        if (fileStart == -1) {
            throw new IllegalArgumentException("Invalid multipart format");
        }
        fileStart += delimiterBytes.length;

        // Пропускаем headers
        int headerEnd = indexOf(requestData, CRLF, fileStart);
        if (headerEnd == -1) {
            throw new IllegalArgumentException("Invalid headers");
        }

        // Парсим filename из Content-Disposition
        String headers = new String(requestData, fileStart, headerEnd - fileStart, StandardCharsets.UTF_8);
        String filename = extractFilename(headers);
        String contentType = extractContentType(headers);

        // Пропускаем пустую строку после headers
        int dataStart = headerEnd + CRLF.length + CRLF.length;

        // Ищем конец файла (следующий delimiter)
        int dataEnd = indexOf(requestData, delimiterBytes, dataStart);
        if (dataEnd == -1) {
            dataEnd = requestData.length;
        }

        // Убираем trailing CRLF
        if (dataEnd > dataStart && endsWith(requestData, dataEnd, CRLF)) {
            dataEnd -= CRLF.length;
        }

        byte[] content = Arrays.copyOfRange(requestData, dataStart, dataEnd);

        return new ParsedFile(filename, contentType, content);
    }

    private String extractFilename(String headers) {
        int fnIdx = headers.indexOf("filename=\"");
        if (fnIdx == -1) {
            throw new IllegalArgumentException("No filename in request");
        }
        int start = fnIdx + 10;
        int end = headers.indexOf("\"", start);
        return headers.substring(start, end);
    }

    private String extractContentType(String headers) {
        int ctIdx = headers.indexOf("Content-Type: ");
        if (ctIdx == -1) {
            return "application/octet-stream";
        }
        int start = ctIdx + 14;
        int end = headers.indexOf("\r\n", start);
        if (end == -1) end = headers.length();
        return headers.substring(start, end).trim();
    }

    private int indexOf(byte[] data, byte[] pattern, int start) {
        for (int i = start; i <= data.length - pattern.length; i++) {
            boolean found = true;
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    private boolean endsWith(byte[] data, int end, byte[] suffix) {
        if (end < suffix.length) return false;
        for (int i = 0; i < suffix.length; i++) {
            if (data[end - suffix.length + i] != suffix[i]) return false;
        }
        return true;
    }
}
