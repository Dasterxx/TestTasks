package doczilla.com.task3.weather.util;

import doczilla.com.task3.weather.model.HourlyForecast;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class ChartGenerator {

    public String generateTemperatureChart(HourlyForecast hourly, String city) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int hours = hourly.getNext24HoursCount();
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < hours; i++) {
            LocalDateTime time = hourly.parseTime(i);
            double temp = hourly.getTemperature().get(i);
            String label = time.format(labelFormatter);
            dataset.addValue(temp, "Temperature", label);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Temperature Forecast for " + city,
                "Time",
                "Temperature (°C)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Стилизация
        chart.setBackgroundPaint(new Color(240, 240, 240));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Настройка оси X (показывать каждые 3 часа)
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // Утолщение линии
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(0, 112, 192));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setDefaultShapesVisible(true);
        plot.setRenderer(renderer);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(baos, chart, 800, 400);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate chart", e);
        }
    }
}