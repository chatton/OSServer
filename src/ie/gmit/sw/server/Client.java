package ie.gmit.sw.server;

import ie.gmit.sw.logging.Log;

public class Client {
    private boolean loggedIn;
    private int id;

    public boolean loggedIn() {
        return loggedIn;
    }

    public void login(int id) {
        this.id = id;
        loggedIn = true;
        Log.info("Client [" + id + "] logging in");
    }

    public int id() {
        return id;
    }
}
