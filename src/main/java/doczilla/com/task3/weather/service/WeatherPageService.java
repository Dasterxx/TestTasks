package doczilla.com.task3.weather.service;

import doczilla.com.task3.weather.config.AppConfig;
import doczilla.com.task3.weather.controller.WeatherView;
import doczilla.com.task3.weather.model.WeatherData;
import doczilla.com.task3.weather.util.ChartGenerator;

public class WeatherPageService {
    private final WeatherService weatherService;
    private final ChartGenerator chartGenerator;

    public WeatherPageService(AppConfig config) {
        this.weatherService = new WeatherService(config);
        this.chartGenerator = new ChartGenerator();
    }

    public WeatherView.WeatherPageData buildPageData(String city) {
        WeatherService.WeatherResult result = weatherService.getWeather(city);
        WeatherData data = result.data();

        String chartBase64 = chartGenerator.generateTemperatureChart(data.getHourly(), city);

        return new WeatherView.WeatherPageData(
                city,
                data,
                chartBase64,
                result.fromCache()
        );
    }
}