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
import java.util.List;
import java.util.stream.Collectors;

public class DisplayMealRecordsCommand extends DatabaseCommand {

    public DisplayMealRecordsCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {
        Log.info("Display Meal Records attempt.");

        if (!client.loggedIn()) {
            Log.warning("User attempted to display meal records but was not logged in.");
            sendMessage("You must be logged in to view records.", Code.FORBIDDEN);
            return; // don't continue with displaying the records.
        }

        try {
            final String messageString = db.getMealRecords(client.id())
                    .stream() // looking at each record for that client
                    .map(this::format) // make it human readable
                    .collect(Collectors.joining(System.lineSeparator())); // join on new line

            sendMessage(messageString, Code.OK);
        } catch (IOException e) {
            Log.error("Error connecting to database. ERROR: " + e);
            sendMessage("Error connecting to Database.", Code.BAD);
        }
    }

    /*
    format a record into a human readable format in order to send it
    back over the socket and be read by the user.
     */
    private String format(MealRecord record) {
        return String.format("Record Id: %s - Type: %s - Description %s",
                record.getId(), record.getMealType(), record.getDesc());
    }
}
