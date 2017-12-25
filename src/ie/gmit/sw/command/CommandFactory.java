package ie.gmit.sw.command;

import ie.gmit.sw.command.impl.AddFitnessRecordCommand;
import ie.gmit.sw.command.impl.AddMealRecordCommand;
import ie.gmit.sw.command.impl.DeleteRecordCommand;
import ie.gmit.sw.command.impl.DisplayFitnessRecordsCommand;
import ie.gmit.sw.command.impl.DisplayMealRecordsCommand;
import ie.gmit.sw.command.impl.ExitCommand;
import ie.gmit.sw.command.impl.LoginCommand;
import ie.gmit.sw.command.impl.MenuCommand;
import ie.gmit.sw.command.impl.RegisterCommand;
import ie.gmit.sw.databases.Database;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.server.Client;
import ie.gmit.sw.server.UserHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CommandFactory {

    private final ObjectInputStream objIn;
    private final ObjectOutputStream objOut;
    private final Client client;
    private final Database db;
    private final UserHandler handler;

    /*
    A CommandFactory is in charge of producing concrete Command implementations
    based on the Code provided.
     */
    public CommandFactory(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client, UserHandler handler) {
        this.objIn = objIn;
        this.objOut = objOut;
        this.db = db;
        this.client = client;
        this.handler = handler;
    }

    public Command makeCommand(final Code code) {
        switch (code) {
            case LOGIN:
                return new LoginCommand(objIn, objOut, db, client);
            case MENU:
                return new MenuCommand(objIn, objOut, client);
            case REGISTER:
                return new RegisterCommand(objIn, objOut, db, client);
            case ADD_FITNESS:
                return new AddFitnessRecordCommand(objIn, objOut, db, client);
            case ADD_MEAL:
                return new AddMealRecordCommand(objIn, objOut, db, client);
            case REQUEST_FITNESS:
                return new DisplayFitnessRecordsCommand(objIn, objOut, db, client);
            case REQUEST_MEAL:
                return new DisplayMealRecordsCommand(objIn, objOut, db, client);
            case DELETE:
                return new DeleteRecordCommand(objIn, objOut, db, client);
            case EXIT:
                return new ExitCommand(handler);
            default:
                throw new IllegalArgumentException("Code: [" + code + "] is not a valid command code.");
        }
    }
}
