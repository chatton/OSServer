package ie.gmit.sw.command;

import ie.gmit.sw.databases.Database;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class DatabaseCommand extends ServerCommand {

    protected Database db;

    public DatabaseCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db) {
        super(objIn, objOut);
        this.db = db;
    }

}
