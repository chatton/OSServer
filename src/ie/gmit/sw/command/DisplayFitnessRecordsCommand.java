package ie.gmit.sw.command;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.records.FitnessRecord;
import ie.gmit.sw.records.RecordType;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.server.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DisplayFitnessRecordsCommand extends DatabaseCommand {

    public DisplayFitnessRecordsCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {
        if (!client.loggedIn()) {
            System.out.println("Client was not logged in. Sending FORBIDDEN.");
            sendMessage(new Message("You must be logged in to view records.", Code.FORBIDDEN));
            return; // don't continue with displaying the records.
        }

        List<FitnessRecord> records;
        try {
            records = db.getNLastFitnessRecords(client.id(), 10);
        } catch (IOException e) {
            sendMessage(new Message("Error connecting to Database.", Code.BAD));
            e.printStackTrace();
            return;
        }
        System.out.println(records);
        StringBuilder sb = new StringBuilder();
        records.forEach(record -> {
            sb.append(String.format("Record Id: %s - Mode: %s - Duration %.2f",
                    record.getId(), record.getMode(), record.getDuration()))
                    .append(System.lineSeparator());
        });
        sendMessage(new Message(sb.toString(), Code.OK));
    }
}
