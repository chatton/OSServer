package ie.gmit.sw.command;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.records.MealRecord;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.server.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class DisplayMealRecordsCommand extends DatabaseCommand {

    public DisplayMealRecordsCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {

        if (!client.loggedIn()) {
            System.out.println("Client was not logged in. Sending FORBIDDEN.");
            sendMessage(new Message("You must be logged in to view records.", Code.FORBIDDEN));
            return; // don't continue with displaying the records.
        }

        List<MealRecord> records;
        try {
            records = db.getMealRecords(client.id());
        } catch (IOException e) {
            sendMessage(new Message("Error connecting to Database.", Code.BAD));
            e.printStackTrace();
            return;
        }

        final StringBuilder sb = new StringBuilder();
        records.forEach(record ->
                sb.append(String.format("Record Id: %s - Type: %s - Description %s",
                        record.getId(), record.getMealType(), record.getDesc()))
                        .append(System.lineSeparator()));

        sendMessage(new Message(sb.toString(), Code.OK));
    }
}
