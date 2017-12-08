package ie.gmit.sw.command;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MenuCommand extends ServerCommand {

    public MenuCommand(ObjectInputStream objIn, ObjectOutputStream objOut) {
        super(objIn, objOut);
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
        sb.append("==============================================").append(System.lineSeparator());
        sendMessage(sb.toString());
    }
}
