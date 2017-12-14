package ie.gmit.sw.server;

import ie.gmit.sw.command.CommandFactory;
import ie.gmit.sw.logging.Log;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.command.Command;
import ie.gmit.sw.databases.Database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static ie.gmit.sw.serialize.Code.EXIT;

public class HandleUser implements Runnable {

    private final ObjectOutputStream objOut;
    private final ObjectInputStream objIn;
    private final Database db;

    public HandleUser(final Socket socket, final Database db) throws IOException {
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objIn = new ObjectInputStream(socket.getInputStream());
        this.db = db;
    }

    private Message readMessage() {
        try {
            Message msg = (Message) objIn.readObject();
            Log.debug("Reading message: " + msg);
            return msg;
        } catch (IOException | ClassNotFoundException e) {
            // return an "Exit" message instead of null
            Log.warning("Unable to read message from user - Most likely user killed client application.");
            return new Message("Error reading out, disconnecting.", EXIT);
        }
    }

    @Override
    public void run() {
        final Client client = new Client(); // represents the connected client.
        final CommandFactory factory = new CommandFactory(objIn, objOut, db, client);
        while (true) { // stay active until user sends EXIT signal or kills the program.
            final Message choice = readMessage();
            if (choice.code() == EXIT) { // socket disconnected or user wants to quit.
                Log.info("User disconnected.");
                closeStreams();
                return;
            }
            // based on the code sent, create the relevant command.
            final Command cmd = factory.makeCommand(choice.code());
            Log.debug("Executing: [" + cmd.getClass().getSimpleName() + "]");
            cmd.execute();
        }
    }

    private void closeStreams() {
        try {
            objIn.close();
            objOut.close();
        } catch (IOException e) {
            Log.warning("Error closing streams. Message:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
