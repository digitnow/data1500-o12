import com.sun.net.httpserver.HttpServer;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import java.net.InetSocketAddress;
import java.io.OutputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        // MongoDB-tilkobling
        MongoClient mongoClient = MongoClients.create("mongodb://mongo:27017");
        StatsController statsController = new StatsController(mongoClient);

        // HTTP-server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Endepunkt for statistikk
        server.createContext("/api/stats", exchange -> {
            try {
                String response = statsController.getStats().toJson();
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                e.printStackTrace();
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server running on port 8080");
    }
}