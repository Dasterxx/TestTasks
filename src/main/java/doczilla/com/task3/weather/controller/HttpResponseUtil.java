package doczilla.com.task3.weather.controller;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class HttpResponseUtil {

    public static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null) return params;

        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2) {
                String key = java.net.URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    public static void sendHtml(HttpExchange exchange, int statusCode, String html) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        send(exchange, statusCode, html);
    }

    public static void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        send(exchange, statusCode, text);
    }

    private static void send(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}