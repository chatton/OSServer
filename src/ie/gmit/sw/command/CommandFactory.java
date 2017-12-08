package ie.gmit.sw.command;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.server.Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CommandFactory {

    private final ObjectInputStream objIn;
    private final ObjectOutputStream objOut;
    private final Client client;
    private final Database db;

    public CommandFactory(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        this.objIn = objIn;
        this.objOut = objOut;
        this.db = db;
        this.client = client;
    }

    public Command makeCommand(Code code) {
        switch (code) {
            case LOGIN:
                return new LoginCommand(objIn, objOut, db, client);
            case MENU:
                return new MenuCommand(objIn, objOut, client);
            case REGISTER:
                return new RegisterCommand(objIn, objOut, db, client);
            default:
                throw new IllegalArgumentException("Code: " + code + " is not a valid command code.");
        }
    }
}
