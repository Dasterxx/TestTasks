package doczilla.com.task3.weather.config;

import doczilla.com.task3.weather.cache.InMemoryCache;

public class AppConfig {
    private final String geocodingUrl = "https://geocoding-api.open-meteo.com/v1/search";
    private final String forecastUrl = "https://api.open-meteo.com/v1/forecast";
    private final int cacheTtlSeconds = 900;
    private final InMemoryCache cache;

    public AppConfig() {
        this.cache = new InMemoryCache(cacheTtlSeconds);
    }

    public InMemoryCache getCache() {
        return cache;
    }

    public String getGeocodingUrl() {
        return geocodingUrl;
    }

    public String getForecastUrl() {
        return forecastUrl;
    }

    public int getCacheTtlSeconds() {
        return cacheTtlSeconds;
    }

    public void shutdown() {
        cache.shutdown();
    }
}