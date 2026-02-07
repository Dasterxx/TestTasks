package doczilla.com.task3.weather.service;

import doczilla.com.task3.weather.cache.InMemoryCache;
import doczilla.com.task3.weather.config.AppConfig;
import doczilla.com.task3.weather.model.CityCoordinates;
import doczilla.com.task3.weather.model.WeatherData;

public class WeatherService {
    private final GeocodingService geocodingService;
    private final ForecastService forecastService;
    private final InMemoryCache cache;

    public WeatherService(AppConfig config) {
        this.geocodingService = new GeocodingService(config);
        this.forecastService = new ForecastService(config);
        this.cache = config.getCache();
    }

    public WeatherResult getWeather(String city) {
        // Проверяем кэш
        WeatherData cached = cache.get(city);
        if (cached != null) {
            System.out.println("Cache HIT for: " + city);
            return new WeatherResult(cached, true);
        }

        System.out.println("Cache MISS for: " + city);

        try {
            CityCoordinates coords = geocodingService.getCoordinates(city);
            System.out.println("Found coordinates: " + coords);

            WeatherData forecast = forecastService.getForecast(
                    coords.getLatitude(),
                    coords.getLongitude()
            );

            // Сохраняем в кэш
            cache.set(city, forecast);

            return new WeatherResult(forecast, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch weather: " + e.getMessage(), e);
        }
    }

    public record WeatherResult(WeatherData data, boolean fromCache) {}
}