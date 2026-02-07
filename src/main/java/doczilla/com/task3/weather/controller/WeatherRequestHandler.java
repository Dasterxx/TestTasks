package doczilla.com.task3.weather.controller;

import java.util.Map;

class WeatherRequestHandler {

    public record WeatherRequest(String city, boolean valid, String errorMessage) {}

    public static WeatherRequest validate(Map<String, String> params) {
        String city = params.get("city");

        if (city == null || city.isBlank()) {
            return new WeatherRequest(null, false, "Missing required parameter: city");
        }

        if (!city.matches("[a-zA-Z\\s-]+")) {
            return new WeatherRequest(null, false, "Invalid city name format");
        }

        return new WeatherRequest(city.trim(), true, null);
    }
}
