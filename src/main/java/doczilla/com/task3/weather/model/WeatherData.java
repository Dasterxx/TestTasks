package doczilla.com.task3.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    private double latitude;
    private double longitude;
    private HourlyForecast hourly;

    @JsonProperty("latitude")
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("hourly")
    public HourlyForecast getHourly() {
        return hourly;
    }

    @JsonProperty("hourly")
    public void setHourly(Hourly hourly) {
        this.hourly = new HourlyForecast(hourly.time(), hourly.temperature_2m());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Hourly(
            java.util.List<String> time,
            java.util.List<Double> temperature_2m
    ) {}
}