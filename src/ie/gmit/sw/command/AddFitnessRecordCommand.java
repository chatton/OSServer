package ie.gmit.sw.command;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.records.FitnessRecord;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.server.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AddFitnessRecordCommand extends DatabaseCommand {

    public AddFitnessRecordCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {

        if (!client.loggedIn()) {
            System.out.println("Client was not logged in. Sending FORBIDDEN.");
            sendMessage(new Message("You must be logged in to add a new fitness record.", Code.FORBIDDEN));
            return; // don't continue with adding the record.
        }

        sendText("Enter Mode: ");
        Message msg = readMessage();
        String mode = msg.message();
        System.out.println("Mode: " + mode);

        sendText("Enter Duration: ");

        try {
            msg = readMessage();
            final double duration = Double.parseDouble(msg.message());
            System.out.println("Duration: " + duration);
            db.addRecord(new FitnessRecord(db.getNextRecordId(client.id()), client.id(), mode, duration));
            System.out.println("Added record.");
            sendMessage(new Message("Added record.", Code.OK));
        } catch (NumberFormatException e) {
            System.out.println("Invalid duration.");
            sendMessage(new Message("Invalid duration provided.", Code.BAD));
        } catch (IOException e) {
            System.out.println("Failed to add record.");
            sendMessage(new Message("Failed adding record.", Code.BAD));
        }

    }
}
