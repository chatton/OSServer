package ie.gmit.sw.command;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.server.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DeleteRecordCommand extends DatabaseCommand {

    public DeleteRecordCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {
        if (!client.loggedIn()) {
            System.out.println("Client was not logged in. Sending FORBIDDEN.");
            sendMessage(new Message("You must be logged in to delete a record.", Code.FORBIDDEN));
            return; // don't continue with adding the record.
        }

        sendText("Enter Record ID to delete:");
        final Message msg = readMessage();
        final String id = msg.message();

        try {
            db.deleteRecord(client.id(), Integer.parseInt(id));
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID entered.");
            sendMessage(new Message("Invalid ID entered.", Code.BAD));
            return;
        } catch (IOException e) {
            sendMessage(new Message("Error deleting record.", Code.BAD));
            return;
        }
        sendMessage(new Message("Deleted record successfully", Code.OK));
    }
}
