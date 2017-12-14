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
import java.util.List;
import java.util.stream.Collectors;

public class DisplayFitnessRecordsCommand extends DatabaseCommand {

    public DisplayFitnessRecordsCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {
        if (!client.loggedIn()) {
            Log.warning("Client was not logged in and attempted to display Fitness Records");
            sendMessage("You must be logged in to view records.", Code.FORBIDDEN);
            return; // don't continue with displaying the records.
        }

        try {
            final String messageString = db.getFitnessRecords(client.id())
                    .stream() // look at relevant records
                    .map(this::format) // make them human readable
                    .collect(Collectors.joining(System.lineSeparator()));// new line separated

            sendMessage(messageString, Code.OK);
        } catch (IOException e) {
            Log.error("Error connecting to database. ERROR: " + e);
            sendMessage("Error connecting to Database.", Code.BAD);
        }


    }

    private String format(FitnessRecord record) {
        return String.format("Record Id: %s - Mode: %s - Duration %.2f",
                record.getId(), record.getMode(), record.getDuration());
    }
}
