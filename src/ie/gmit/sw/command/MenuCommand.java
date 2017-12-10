package ie.gmit.sw.command;

import ie.gmit.sw.server.Client;
import ie.gmit.sw.serialize.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static ie.gmit.sw.serialize.Code.OK;

public class MenuCommand extends ServerCommand {

    public MenuCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Client client) {
        super(objIn, objOut, client);
    }

    @Override
    public void execute() {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================").append(System.lineSeparator());
        sb.append("1. - Register with the system.").append(System.lineSeparator());
        sb.append("2. - Login to the system.").append(System.lineSeparator());
        sb.append("3. - Add fitness record.").append(System.lineSeparator());
        sb.append("4. - Add meal record.").append(System.lineSeparator());
        sb.append("5. - View last 10 records.").append(System.lineSeparator());
        sb.append("6. - View the last 10 fitness records.").append(System.lineSeparator());
        sb.append("7. - Delete a record.").append(System.lineSeparator());
        sb.append("8. - Exit.").append(System.lineSeparator());
        sb.append("==============================================").append(System.lineSeparator());
        sendMessage(new Message(sb.toString(), OK));
    }
}
