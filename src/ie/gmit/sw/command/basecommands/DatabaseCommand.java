package ie.gmit.sw.command.basecommands;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.server.Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
A Database command is identical to a ServerCommand but has a reference
to a Database object. For any commands that are required to modify the database.
 */
public abstract class DatabaseCommand extends ServerCommand {

    protected Database db;

    public DatabaseCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, client);
        this.db = db;
    }

}
