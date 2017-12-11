package ie.gmit.sw;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.server.Server;

import java.io.IOException;

public class ServerRunner {
    public static void main(String[] args) throws IOException {
        final Database db = new Database();
        final Server server = new Server(9090, db);
        server.start();
    }

}

