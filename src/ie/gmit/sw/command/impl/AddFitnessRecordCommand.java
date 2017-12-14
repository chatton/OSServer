package ie.gmit.sw.command.impl;

import ie.gmit.sw.command.basecommands.DatabaseCommand;
import ie.gmit.sw.databases.Database;
import ie.gmit.sw.logging.Log;
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
            Log.warning("Client was not logged in and attempted to add a new Fitness Record");
            sendMessage("You must be logged in to add a new fitness record.", Code.FORBIDDEN);
            return; // don't continue with adding the record.
        }

        sendMessage("Enter Mode: ");
        Message msg = readMessage();
        String mode = msg.message();
        Log.info("Entered Mode: " + mode);
        sendMessage("Enter Duration: ");

        try {
            msg = readMessage();
            final double duration = Double.parseDouble(msg.message());
            Log.info("Entered Duration: " + duration);
            final boolean added = db.addRecord(new FitnessRecord(db.getNextRecordId(client.id()), client.id(), mode, duration));
            if (added) {
                Log.info("Added record successfully.");
                sendMessage("Added record.", Code.OK);
            } else {
                Log.info("Adding record failed.");
                sendMessage("Failed to add record.", Code.BAD);
            }
        } catch (NumberFormatException e) {
            Log.warning("Invalid duration provided.");
            sendMessage("Invalid duration provided.", Code.BAD);
        } catch (IOException e) {
            Log.error("Failed to add record.");
            sendMessage("Failed adding record.", Code.BAD);
        }
    }
}
