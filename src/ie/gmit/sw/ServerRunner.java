package ie.gmit.sw;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.logging.Log;
import ie.gmit.sw.server.Server;

import java.io.IOException;

public class ServerRunner {

    public static void main(String[] args) {
        checkDebugMode(args);
        startServer();
    }

    private static void checkDebugMode(String[] args) {
        if (args.length > 0) {
            final String debug = args[0];
            if (debug.equalsIgnoreCase("debug")) {
                Log.toggleDebugMode();
            }
        }
    }

    private static void startServer() {
        try {
            final Database db = new Database();
            final Server server = new Server(9090, db);
            server.start();
        } catch (IOException e) {
            Log.error("Error starting server: " + e.getMessage());
        }
    }

}

