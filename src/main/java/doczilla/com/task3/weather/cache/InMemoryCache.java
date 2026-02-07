package doczilla.com.task3.weather.cache;

import doczilla.com.task3.weather.model.WeatherData;

import java.time.Instant;
import java.util.concurrent.*;

public class InMemoryCache {
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner;
    private final int ttlSeconds;

    private record CacheEntry(WeatherData data, Instant expiresAt) {}

    public InMemoryCache(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
        this.cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "cache-cleaner");
            t.setDaemon(true);
            return t;
        });

        // Очистка каждые 5 минут
        this.cleaner.scheduleAtFixedRate(this::cleanup, ttlSeconds, 300, TimeUnit.SECONDS);
    }

    public WeatherData get(String city) {
        CacheEntry entry = cache.get(city.toLowerCase());
        if (entry == null) return null;

        if (Instant.now().isAfter(entry.expiresAt())) {
            cache.remove(city.toLowerCase());
            return null;
        }

        return entry.data();
    }

    public void set(String city, WeatherData data) {
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);
        cache.put(city.toLowerCase(), new CacheEntry(data, expiresAt));
    }

    private void cleanup() {
        Instant now = Instant.now();
        cache.entrySet().removeIf(e -> now.isAfter(e.getValue().expiresAt()));
    }

    public void shutdown() {
        cleaner.shutdown();
    }
}