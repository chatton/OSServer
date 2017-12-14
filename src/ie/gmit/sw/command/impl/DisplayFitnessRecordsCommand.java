package ie.gmit.sw.command.impl;

import ie.gmit.sw.command.basecommands.DatabaseCommand;
import ie.gmit.sw.databases.Database;
import ie.gmit.sw.records.FitnessRecord;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.server.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.Collectors;

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
            records = db.getFitnessRecords(client.id());
        } catch (IOException e) {
            sendMessage(new Message("Error connecting to Database.", Code.BAD));
            e.printStackTrace();
            return;
        }

        final String messageString = records.stream()
                .map(this::format)
                .collect(Collectors.joining(System.lineSeparator()));

        sendMessage(new Message(messageString, Code.OK));
    }

    private String format(FitnessRecord record) {
        return String.format("Record Id: %s - Mode: %s - Duration %.2f",
                record.getId(), record.getMode(), record.getDuration());
    }
}
