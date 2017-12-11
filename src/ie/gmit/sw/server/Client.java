package ie.gmit.sw.server;

public class Client {
    private boolean loggedIn;
    private int id;

    public boolean loggedIn() {
        return loggedIn;
    }

    public void login(int id) {
        this.id = id;
        loggedIn = true;
    }

    public int id() {
        return id;
    }
}
