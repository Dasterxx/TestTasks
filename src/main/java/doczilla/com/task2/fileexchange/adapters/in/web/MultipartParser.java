package doczilla.com.task2.fileexchange.adapters.in.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MultipartParser {

    public ParsedFile parse(InputStream inputStream, String contentTypeHeader) throws IOException {
        // Извлекаем boundary из Content-Type заголовка
        String boundary = extractBoundary(contentTypeHeader);
        if (boundary == null) {
            throw new IllegalArgumentException("No boundary found");
        }

        // Читаем весь запрос
        byte[] requestData = inputStream.readAllBytes();

        String boundaryMarker = "--" + boundary;
        byte[] boundaryBytes = boundaryMarker.getBytes(StandardCharsets.UTF_8);

        // Находим первое вхождение boundary
        int partStart = indexOf(requestData, boundaryBytes, 0);
        if (partStart == -1) {
            throw new IllegalArgumentException("Invalid multipart format: no boundary");
        }

        // Пропускаем первый boundary и CRLF
        partStart += boundaryBytes.length;
        if (partStart < requestData.length && requestData[partStart] == '\r' && requestData[partStart + 1] == '\n') {
            partStart += 2;
        }

        // Находим конец части (следующий boundary)
        int partEnd = indexOf(requestData, boundaryBytes, partStart);
        if (partEnd == -1) {
            partEnd = requestData.length;
        }

        // Выделяем часть с файлом
        byte[] partData = Arrays.copyOfRange(requestData, partStart, partEnd);

        // Парсим заголовки части
        return parsePart(partData);
    }

    private String extractBoundary(String contentType) {
        if (contentType == null) return null;

        int idx = contentType.indexOf("boundary=");
        if (idx == -1) return null;

        String boundary = contentType.substring(idx + 9);
        // Убираем кавычки если есть
        boundary = boundary.replace("\"", "").trim();

        return boundary;
    }

    private ParsedFile parsePart(byte[] partData) throws IOException {
        // Ищем конец заголовков (пустая строка)
        int headerEnd = 0;
        for (int i = 0; i < partData.length - 3; i++) {
            if (partData[i] == '\r' && partData[i+1] == '\n' &&
                    partData[i+2] == '\r' && partData[i+3] == '\n') {
                headerEnd = i;
                break;
            }
        }

        if (headerEnd == 0) {
            throw new IllegalArgumentException("Invalid part format: no header end");
        }

        // Парсим заголовки
        String headers = new String(partData, 0, headerEnd, StandardCharsets.UTF_8);
        String filename = extractFilename(headers);
        String contentType = extractContentType(headers);

        // Данные файла начинаются после пустой строки
        int dataStart = headerEnd + 4; // \r\n\r\n

        // Убираем trailing CRLF если есть
        int dataEnd = partData.length;
        if (dataEnd > dataStart + 2 &&
                partData[dataEnd-2] == '\r' && partData[dataEnd-1] == '\n') {
            dataEnd -= 2;
        }

        byte[] content = Arrays.copyOfRange(partData, dataStart, dataEnd);

        return new ParsedFile(filename, contentType, content);
    }

    private String extractFilename(String headers) {
        // Content-Disposition: form-data; name="file"; filename="test.txt"
        int fnIdx = headers.indexOf("filename=\"");
        if (fnIdx == -1) {
            // Пробуем без кавычек
            fnIdx = headers.indexOf("filename=");
            if (fnIdx == -1) {
                throw new IllegalArgumentException("No filename in request");
            }
            int start = fnIdx + 9;
            int end = headers.indexOf("\r\n", start);
            if (end == -1) end = headers.length();
            return headers.substring(start, end).trim();
        }

        int start = fnIdx + 10;
        int end = headers.indexOf("\"", start);
        if (end == -1) {
            throw new IllegalArgumentException("Unclosed filename");
        }

        return headers.substring(start, end);
    }

    private String extractContentType(String headers) {
        int ctIdx = headers.indexOf("Content-Type:");
        if (ctIdx == -1) {
            return "application/octet-stream";
        }

        int start = ctIdx + 13;
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
}