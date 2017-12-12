package ie.gmit.sw;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.logging.Log;
import ie.gmit.sw.server.Server;

import java.io.IOException;

public class ServerRunner {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            final String debug = args[1];
            if (debug.equalsIgnoreCase("true")) {
                Log.toggleDebugMode();
            }
        }

        final Database db = new Database();
        final Server server = new Server(9090, db);
        server.start();
    }

}

