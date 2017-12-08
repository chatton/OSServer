package ie.gmit.sw.server;

import ie.gmit.sw.command.CommandFactory;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.command.Command;
import ie.gmit.sw.command.LoginCommand;
import ie.gmit.sw.command.MenuCommand;
import ie.gmit.sw.command.RegisterCommand;
import ie.gmit.sw.databases.Database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static ie.gmit.sw.serialize.Code.BAD;

public class HandleUser implements Runnable {

    private final ObjectOutputStream objOut;
    private final ObjectInputStream objIn;
    private final Database db;

    public HandleUser(final Socket socket, final Database db) throws IOException {
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objIn = new ObjectInputStream(socket.getInputStream());
        this.db = db;
    }

    public Message readMessage() {
        try {
            return (Message) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new Message("unable to read message", BAD);
        }
    }

    @Override
    public void run() {
        final Client client = new Client();
        final CommandFactory factory = new CommandFactory(objIn, objOut, db, client);
        while (true) {
            final Message choice = readMessage();
            if (choice.code() == BAD) { // socket disconnected.
                System.out.println("User disconnected.");
                return;
            }

            Command cmd = factory.makeCommand(choice.code());
            cmd.execute();
        }
    }
}
