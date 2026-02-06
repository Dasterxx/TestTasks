package doczilla.com.task2.fileexchange;

import com.sun.net.httpserver.HttpServer;
import doczilla.com.task2.fileexchange.adapters.config.ApplicationConfig;

public class FileExchangeApplication {

    public static void main(String[] args) throws Exception {
        ApplicationConfig config = new ApplicationConfig();
        HttpServer server = config.compose().server();

        server.start();
        System.out.println("File Exchange Service running on port 8080");
        System.out.println("Using Virtual Threads: enabled");
    }
}
