package doczilla.com.task3.weather.service;

import doczilla.com.task3.weather.config.AppConfig;
import doczilla.com.task3.weather.model.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class ForecastService {
    private final AppConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ForecastService(AppConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public WeatherData getForecast(double lat, double lon) throws Exception {
        // Используем Locale.US для точки вместо запятой
        String url = String.format(Locale.US, "%s?latitude=%.4f&longitude=%.4f&hourly=temperature_2m&timezone=auto",
                config.getForecastUrl(), lat, lon);

        System.out.println("Forecast URL: " + url); // Для отладки

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Forecast API response: " + response.body());
            throw new RuntimeException("Forecast API error: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), WeatherData.class);
    }
}