package ie.gmit.sw.command.impl;

import ie.gmit.sw.command.basecommands.DatabaseCommand;
import ie.gmit.sw.logging.Log;
import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.server.Client;
import ie.gmit.sw.server.User;
import ie.gmit.sw.databases.Database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RegisterCommand extends DatabaseCommand {

    public RegisterCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    private boolean nameOk(String name) {
        try {
            return db.nameAvailable(name);
        } catch (IOException e) {
            Log.error("Error reading from database. ERROR: " + e);
            return false;
        }
    }

    private boolean passwordOk(String password) {
        return !password.contains(" ") && !password.isEmpty();
    }

    private boolean ppsnOk(String ppsn) {
        return ppsn.matches("^\\d+[a-zA-Z]+$");
    }

    private boolean doubleOkay(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean intOkay(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private boolean replyToUser(boolean condition, String okayText, String badText) {
        if (condition) {
            sendMessage(new Message(okayText, Code.OK));
            return true;
        } else {
            sendMessage(new Message(badText, Code.BAD));
            return false;
        }
    }

    @Override
    public void execute() {
        Log.info("Registration attempt.");
        sendMessage("Enter user name: ");
        Message userNameMessage = readMessage();
        String userName = userNameMessage.message();
        Log.info("Username: " + userName);
        boolean keepGoing = replyToUser(nameOk(userNameMessage.message()), "User name accepted.", "Bad user name.");

        if (!keepGoing) {
            return;
        }

        sendMessage("Enter your password: ");
        Message passwordMessage = readMessage();
        final int passHash = passwordMessage.message().hashCode();
        Log.info("Password: " + passwordMessage.message());
        keepGoing = replyToUser(passwordOk(passwordMessage.message()), "Password okay.", "Password invalid.");

        if (!keepGoing) {
            return;
        }

        sendMessage("Enter address: ");
        Message addressMessage = readMessage();
        final String address = addressMessage.message();
        Log.info("Address: " + address);
        keepGoing = replyToUser(true, "Address Okay.", "Bad address");
        if (!keepGoing) {
            return;
        }

        sendMessage("Enter ppsn: ");
        final Message ppsnMessage = readMessage();
        final String ppsn = ppsnMessage.message();
        Log.info("PPSN: " + ppsn);
        keepGoing = replyToUser(ppsnOk(ppsnMessage.message()), "PPSN Okay.", "PPSN invalid.");

        if (!keepGoing) {
            return;
        }

        sendMessage("Enter height: ");
        Message heightMessage = readMessage();
        Log.info("Height: " + heightMessage.message());
        keepGoing = replyToUser(doubleOkay(heightMessage.message()), "Height Okay.", "Height invalid.");

        if (!keepGoing) {
            return;
        }

        double height = Double.parseDouble(heightMessage.message());

        sendMessage("Enter weight: ");
        Message weightMessage = readMessage();
        Log.info("Weight: " + weightMessage.message());
        keepGoing = replyToUser(doubleOkay(weightMessage.message()), "Weight Okay.", "Weight invalid.");

        if (!keepGoing) {
            return;
        }

        double weight = Double.parseDouble(weightMessage.message());

        sendMessage("Enter age: ");
        Message ageMessage = readMessage();
        Log.info("Age: " + ageMessage.message());
        keepGoing = replyToUser(doubleOkay(ageMessage.message()), "Age Okay.", "Age invalid.");

        if (!keepGoing) {
            return;
        }

        int age = Integer.parseInt(ageMessage.message());
        final User user = new User(userName, address, passHash, ppsn, height, weight, age);

        try {
            Log.info("Adding user: " + user);
            db.addUser(user);
            Log.info("Successfully added user.");
            sendCode(Code.OK);
        } catch (IOException e) {
            Log.error("Error adding user: " + user + " ERROR: " + e);
            sendCode(Code.BAD);
        }
    }
}
