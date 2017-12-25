package ie.gmit.sw.server;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.logging.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final Database db;
    private ExecutorService executor;
    private volatile boolean running = true;

    public Server(final int port, final Database db) {
        Log.info("Creating server instance.");
        this.port = port;
        this.db = db;
        executor = Executors.newCachedThreadPool();
    }

    public void stop() {
        running = false;
    }

    public void start() throws IOException {

        final ServerSocket ss = new ServerSocket(port);

        while (running) {
            Log.info("Listening for connection.");
            final Socket socket = ss.accept();
            Log.info("Connection received.");
            executor.submit(new UserHandler(socket, db));
        }

        Log.info("Server shutting down.");
        executor.shutdown();
    }
}
