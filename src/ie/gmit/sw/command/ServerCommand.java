package ie.gmit.sw.command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ServerCommand implements Command {
    private final ObjectOutputStream objOut;
    private final ObjectInputStream objIn;

    public ServerCommand(ObjectInputStream objIn, ObjectOutputStream objOut) {
        this.objOut = objOut;
        this.objIn = objIn;
    }

    protected void sendMessage(String message) {
        try {
            objOut.writeObject(message);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String readMessage() {
        try {
            return (String) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String sendAndReceive(final String message) {
        sendMessage(message);
        return readMessage();
    }
}
