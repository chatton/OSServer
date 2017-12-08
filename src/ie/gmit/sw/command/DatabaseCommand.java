package ie.gmit.sw.command;

import ie.gmit.sw.server.Client;
import ie.gmit.sw.databases.Database;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class DatabaseCommand extends ServerCommand {

    protected Database db;

    public DatabaseCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, client);
        this.db = db;
    }

}
