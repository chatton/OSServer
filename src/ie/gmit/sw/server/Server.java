package ie.gmit.sw.server;

import ie.gmit.sw.databases.Database;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final Database db;
    private ServerSocket ss;
    private ExecutorService executor;
    private volatile boolean running = true;

    public Server(final int port) {
        this.port = port;
        db = new Database();
        executor = Executors.newCachedThreadPool();
    }

    public void stop() {
        running = false;
    }

    public void start() throws IOException {
        ss = new ServerSocket(port);

        while (running) {
            System.out.println("Listening for connection.");
            final Socket socket = ss.accept();
            System.out.println("Connection received!");
            executor.submit(new HandleUser(socket, db));
        }

        System.out.println("Terminating...");
        executor.shutdown();
    }


}
