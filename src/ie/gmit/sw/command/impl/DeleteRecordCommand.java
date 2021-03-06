package ie.gmit.sw.command.impl;

import ie.gmit.sw.command.basecommands.DatabaseCommand;
import ie.gmit.sw.databases.Database;
import ie.gmit.sw.logging.Log;
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
            Log.warning("Client was not logged in and tried to delete record.");
            sendMessage(new Message("You must be logged in to delete a record.", Code.FORBIDDEN));
            return; // don't continue with adding the record.
        }

        sendMessage("Enter Record ID to delete:");
        final Message msg = readMessage();
        final String id = msg.message();

        boolean deleted;
        try {
            deleted = db.deleteRecord(client.id(), Integer.parseInt(id));
        } catch (NumberFormatException e) {
            Log.warning("Invalid ID entered.");
            sendMessage("Invalid ID entered.", Code.BAD);
            return;
        } catch (IOException e) {
            Log.error("Failed writing to database.");
            sendMessage("Error deleting record from database.", Code.BAD);
            return;
        }

        if (deleted) {
            Log.info("Successfully deleted record.");
            sendMessage("Deleted record successfully", Code.OK);
        } else {
            final String errorMsg = "Deleting record [" + id + "] failed.";
            Log.error(errorMsg);
            sendMessage(errorMsg, Code.BAD);
        }
    }
}
