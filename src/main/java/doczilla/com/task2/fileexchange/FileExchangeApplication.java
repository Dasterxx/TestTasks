package doczilla.com.task2.fileexchange;

import doczilla.com.task2.fileexchange.adapters.config.ApplicationConfig;
import doczilla.com.task2.fileexchange.adapters.in.web.StaticFileServer;

public class FileExchangeApplication {

    public static void main(String[] args) throws Exception {
        // Backend
        ApplicationConfig config = new ApplicationConfig();
        var app = config.compose();
        app.start();

        // Frontend (опционально)
        new StaticFileServer("frontend", 3000).start();

        System.out.println("\n=================================");
        System.out.println("Backend: http://localhost:8080");
        System.out.println("Frontend: http://localhost:3000");
        System.out.println("=================================");
    }
}
