package doczilla.com.task3.weather.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import doczilla.com.task3.weather.config.AppConfig;
import doczilla.com.task3.weather.service.WeatherPageService;

import java.io.IOException;
import java.util.Map;

public class WeatherController implements HttpHandler {
    private final WeatherPageService pageService;
    private final WeatherView view;

    public WeatherController(AppConfig config) {
        this.pageService = new WeatherPageService(config);
        this.view = new WeatherView();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponseUtil.sendText(exchange, 405, "Method not allowed");
            return;
        }

        Map<String, String> params = HttpResponseUtil.parseQuery(exchange.getRequestURI().getQuery());
        WeatherRequestHandler.WeatherRequest request = WeatherRequestHandler.validate(params);

        if (!request.valid()) {
            HttpResponseUtil.sendHtml(exchange, 400, view.renderError("Bad Request", request.errorMessage()));
            return;
        }

        try {
            WeatherView.WeatherPageData pageData = pageService.buildPageData(request.city());
            String html = view.render(pageData);
            HttpResponseUtil.sendHtml(exchange, 200, html);

        } catch (IllegalArgumentException e) {
            HttpResponseUtil.sendHtml(exchange, 404, view.renderError("City Not Found",
                    "City '" + request.city() + "' not found. Please check the spelling."));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            HttpResponseUtil.sendHtml(exchange, 500, view.renderError("Server Error",
                    "Something went wrong. Please try again later."));
        }
    }
}