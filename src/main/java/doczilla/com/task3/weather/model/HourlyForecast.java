package doczilla.com.task3.weather.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HourlyForecast {
    private final List<String> time;
    private final List<Double> temperature;

    public HourlyForecast(List<String> time, List<Double> temperature) {
        this.time = time;
        this.temperature = temperature;
    }

    public List<String> getTime() {
        return time;
    }

    public List<Double> getTemperature() {
        return temperature;
    }

    public int getNext24HoursCount() {
        return Math.min(24, time.size());
    }

    public LocalDateTime parseTime(int index) {
        return LocalDateTime.parse(time.get(index), DateTimeFormatter.ISO_DATE_TIME);
    }
}