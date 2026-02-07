package doczilla.com.task3.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import doczilla.com.task3.weather.config.AppConfig;
import doczilla.com.task3.weather.model.CityCoordinates;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeocodingService {
    private final AppConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeocodingService(AppConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CityCoordinates getCoordinates(String city) throws Exception {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = config.getGeocodingUrl() + "?name=" + encodedCity + "&count=1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Geocoding API error: " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode results = root.get("results");

        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("City not found: " + city);
        }

        return objectMapper.treeToValue(results.get(0), CityCoordinates.class);
    }
}