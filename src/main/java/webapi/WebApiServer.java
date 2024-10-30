package webapi;

import org.apache.log4j.chainsaw.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.ClientController;
import io.javalin.Javalin;

public class WebApiServer {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        Javalin app = startServer(args);
        
        ClientController controller = new ClientController();

        app.get("/api/greeting", ctx -> {
            ctx.json(new Greeting("Hello from Javalin Server with CORS!"));
        });

        
        app.post("/api/echo/{data}", ctx -> {
            String receivedData = ctx.pathParam("data");
            ctx.result("Echo: " + receivedData);
        });

        // get world
        app.get("/world", ctx -> {
            // ctx.json(new Greeting("request for world received"));
            ctx.json(controller.dumpWorld());

        });

        // get world
        app.get("/world/{data}", ctx -> {
            String receivedData = ctx.pathParam("data");
            ctx.json(controller.fetchWorldFromDatabase(receivedData));
        });

        // launch for robot
        app.post("/robot/launch", ctx -> {
            String receivedData = ctx.body();
            ctx.json(controller.handleCommand(receivedData));
        });

        // look for robot
        app.post("/robot/look", ctx -> {
            String receivedData = ctx.body();
            ctx.json(controller.handleCommand(receivedData));
        });

        // Log incoming requests
        app.before(ctx -> {
            logger.info("Received {} request to {}", ctx.method(), ctx.url().toString());
        });

        // Log outgoing responses
        app.after(ctx -> {
            logger.info("Responded with status {}", ctx.status());
        });
    }

    public static class Greeting {
        public String message;

        public Greeting(String message) {
            this.message = message;
        }
    }

    static public Javalin startServer (String[] args) {
        Javalin app;
        try {
            app = Javalin.create(config -> {
            }).start(Integer.parseInt(args[0]));
        } catch (Exception e) {
            app = Javalin.create(config -> {
            }).start(7000);
        }
        return app;
    }
}