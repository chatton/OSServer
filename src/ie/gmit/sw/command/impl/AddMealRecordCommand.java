package ie.gmit.sw.command.impl;

import ie.gmit.sw.command.basecommands.DatabaseCommand;
import ie.gmit.sw.databases.Database;
import ie.gmit.sw.logging.Log;
import ie.gmit.sw.records.MealRecord;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.server.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AddMealRecordCommand extends DatabaseCommand {

    public AddMealRecordCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {
        if (!client.loggedIn()) {
            Log.warning("User tried to add a new meal record but was not logged in.");
            sendMessage(new Message("You must be logged in to add a new meal record.", Code.FORBIDDEN));
            return; // don't continue with adding the record.
        }


        sendText("Enter type of meal: ");
        Message msg = readMessage();
        String meal = msg.message();
        Log.info("Meal Type: " + meal);

        sendText("Enter Description: ");
        msg = readMessage();
        String desc = msg.message();
        Log.info("Description: " + desc);

        try {
            db.addRecord(new MealRecord(db.getNextRecordId(client.id()), client.id(), meal, desc));
            Log.info("Added record.");
            sendMessage(new Message("Added record.", Code.OK));
        } catch (IOException e) {
            Log.error("Failed to add record. ERROR: " + e);
            sendMessage(new Message("Failed adding record.", Code.BAD));
        }
    }
}
