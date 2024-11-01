package webapi;

import org.apache.log4j.chainsaw.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.javalin.http.staticfiles.Location;
import io.javalin.Javalin;

import domain.ClientController;

public class WebApiServer {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        Javalin app = startServer(args);
        
        ClientController controller = new ClientController();

        app.get("/api/greeting", ctx -> {
            ctx.json(new Greeting("Hello from Javalin Server with CORS!"));
        });

        app.get("/", ctx -> {
            ctx.redirect("/html/index.html");
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
        
        app = Javalin.create(config -> {
        
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.directory = "src/main/resources/static";
                staticFileConfig.location = Location.EXTERNAL;
            });
        
        });
        
        return app.start(7000);
    }
}
