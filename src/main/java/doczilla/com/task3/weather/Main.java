package doczilla.com.task3.weather;

import doczilla.com.task3.weather.config.AppConfig;
import doczilla.com.task3.weather.controller.WeatherController;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Main {
    public static void main(String[] args) throws IOException {
        AppConfig config = new AppConfig();
        WeatherController controller = new WeatherController(config);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/weather", controller);
        server.setExecutor(null);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down...");
            server.stop(0);
            config.shutdown();
        }));

        System.out.println("Weather service started on port 8080");
        System.out.println("Try: http://localhost:8080/weather?city=Moscow");
        System.out.println("Press Ctrl+C to stop");

        server.start();
    }
}