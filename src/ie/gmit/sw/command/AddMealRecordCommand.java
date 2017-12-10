package ie.gmit.sw.command;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.records.FitnessRecord;
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
            System.out.println("Client was not logged in. Sending FORBIDDEN.");
            sendMessage(new Message("You must be logged in to add a new meal record.", Code.FORBIDDEN));
            return; // don't continue with adding the record.
        }


        sendText("Enter type of meal: ");
        Message msg = readMessage();
        String meal = msg.message();
        System.out.println("Meal Type: " + meal);

        sendText("Enter Description: ");
        msg = readMessage();
        String desc = msg.message();
        System.out.println("Description: " + desc);

        try {
            db.addRecord(new MealRecord(client.id(), meal, desc));
            System.out.println("Added record.");
            sendMessage(new Message("Added record.", Code.OK));
        } catch (IOException e) {
            System.out.println("Failed to add record.");
            sendMessage(new Message("Failed adding record.", Code.BAD));
        }
    }
}
