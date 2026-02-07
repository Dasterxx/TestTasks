package doczilla.com.task3.weather.controller;

import doczilla.com.task3.weather.model.HourlyForecast;
import doczilla.com.task3.weather.model.WeatherData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherView {

    public String render(WeatherPageData data) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Weather in %s</title>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; max-width: 900px; margin: 0 auto; padding: 20px; background: #f5f5f5; }
                    .container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    h1 { color: #333; margin-bottom: 10px; }
                    .subtitle { color: #666; margin-bottom: 20px; }
                    .cache-badge { display: inline-block; padding: 5px 15px; border-radius: 20px; font-size: 0.85em; font-weight: bold; %s }
                    .chart-container { margin: 25px 0; text-align: center; background: #fafafa; padding: 20px; border-radius: 8px; }
                    .chart-container img { max-width: 100%%; height: auto; border-radius: 4px; }
                    table { width: 100%%; border-collapse: collapse; margin-top: 20px; }
                    th { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 12px; text-align: left; }
                    td { padding: 10px; border-bottom: 1px solid #ddd; }
                    tr:hover { background-color: #f5f5f5; }
                    .temp-positive { color: #e74c3c; font-weight: bold; }
                    .temp-negative { color: #3498db; font-weight: bold; }
                    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; color: #999; font-size: 0.9em; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🌤️ %s</h1>
                    <div class="subtitle">Weather Forecast • Next 24 Hours</div>
                    <span class="cache-badge">%s</span>
                    
                    <div class="chart-container">
                        <img src="data:image/png;base64,%s" alt="Temperature Chart">
                    </div>
                    
                    <h2>Hourly Details</h2>
                    <table>
                        <thead>
                            <tr><th>Time</th><th>Temperature</th></tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>
                    
                    <div class="footer">
                        📍 Coordinates: %.4f, %.4f • Generated at %s
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                data.city(),
                data.fromCache() ? "background: #d4edda; color: #155724;" : "background: #fff3cd; color: #856404;",
                data.city(),
                data.fromCache() ? "✅ Cached" : "🔄 Live Data",
                data.chartBase64(),
                renderTableRows(data.weatherData().getHourly()),
                data.weatherData().getLatitude(),
                data.weatherData().getLongitude(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
    }

    private String renderTableRows(HourlyForecast hourly) {
        StringBuilder rows = new StringBuilder();
        int hours = Math.min(24, hourly.getTime().size());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < hours; i++) {
            LocalDateTime time = hourly.parseTime(i);
            double temp = hourly.getTemperature().get(i);
            String tempClass = temp >= 0 ? "temp-positive" : "temp-negative";
            String tempIcon = temp >= 0 ? "🌡️" : "❄️";

            rows.append(String.format(
                    "<tr><td>%s</td><td class=\"%s\">%s %.1f°C</td></tr>",
                    time.format(timeFormatter),
                    tempClass,
                    tempIcon,
                    temp
            ));
        }

        return rows.toString();
    }

    public String renderError(String title, String message) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 600px; margin: 100px auto; text-align: center; }
                    .error-box { background: #fee; border: 1px solid #fcc; padding: 40px; border-radius: 10px; }
                    h1 { color: #c33; }
                    a { color: #667eea; }
                </style>
            </head>
            <body>
                <div class="error-box">
                    <h1>⚠️ %s</h1>
                    <p>%s</p>
                    <p><a href="/weather?city=Moscow">Try Moscow</a> or <a href="/weather?city=London">London</a></p>
                </div>
            </body>
            </html>
            """.formatted(title, title, message);
    }

    public record WeatherPageData(
            String city,
            WeatherData weatherData,
            String chartBase64,
            boolean fromCache
    ) {}
}
