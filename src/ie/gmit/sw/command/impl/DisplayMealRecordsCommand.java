package ie.gmit.sw.command.impl;

import ie.gmit.sw.command.basecommands.DatabaseCommand;
import ie.gmit.sw.databases.Database;
import ie.gmit.sw.logging.Log;
import ie.gmit.sw.records.MealRecord;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.server.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static ie.gmit.sw.util.ListUtils.nLast;

public class DisplayMealRecordsCommand extends DatabaseCommand {

    public DisplayMealRecordsCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {
        if (!client.loggedIn()) {
            Log.warning("User attempted to display meal records but was not logged in.");
            sendMessage("You must be logged in to view records.", Code.FORBIDDEN);
            return; // don't continue with displaying the records.
        }

        try {
            final List<MealRecord> records = db.getMealRecords(client.id());
            final String messageString = nLast(10, records) // look at just the last 10 records.
                    .stream()
                    .map(this::format) // make it human readable.
                    .reduce((rec1, rec2) -> rec1 + System.lineSeparator() + rec2) // build up string of formatted descriptions
                    .orElse("No meal records Available."); // if there aren't any, indicate no records

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
