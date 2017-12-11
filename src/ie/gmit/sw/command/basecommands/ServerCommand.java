package ie.gmit.sw.command.basecommands;

import ie.gmit.sw.command.Command;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.server.Client;
import ie.gmit.sw.serialize.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
The ServerCommand is an abstract base class that provides base functionality
to send and receive Message objects from the Object Input and Output streams provided.
 */
public abstract class ServerCommand implements Command {
    private final ObjectOutputStream objOut;
    private final ObjectInputStream objIn;
    protected final Client client;

    public ServerCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Client client) {
        this.objOut = objOut;
        this.objIn = objIn;
        this.client = client;
    }

    protected void sendText(String text) {
        sendMessage(new Message(text, Code.MESSAGE));
    }

    protected void sendCode(Code code) {
        sendMessage(new Message("", code));
    }

    protected void sendMessage(Message message) {
        try {
            objOut.writeObject(message);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Message readMessage() {
        try {
            return (Message) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new Message("Error reading message", Code.BAD);
        }
    }

}