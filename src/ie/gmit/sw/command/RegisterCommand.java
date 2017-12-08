package ie.gmit.sw.command;

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
            return false;
        }
    }

    private boolean passwordOk(String password) {
        return !password.contains(" ");
    }

    private boolean ppsnOk(String ppsn) {
        return ppsn.matches("^\\d+[a-zA-Z]+$");
    }

    private boolean doubleOkay(String number) {
        return number.matches("[\\d]+\\.?\\d+");
    }

    private boolean intOkay(String number) {
        return number.matches("^\\d+$");
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
        sendText("Enter user name: ");
        Message userNameMessage = readMessage();
        String userName = userNameMessage.message();
        System.out.println("Username: " + userName);
        boolean keepGoing = replyToUser(nameOk(userNameMessage.message()), "User name accepted.", "Bad user name.");

        if (!keepGoing) {
            return;
        }

        sendText("Enter your password: ");
        Message passwordMessage = readMessage();
        int passHash = passwordMessage.message().hashCode();
        System.out.println("Password: " + passwordMessage.message());
        keepGoing = replyToUser(passwordOk(passwordMessage.message()), "Password okay.", "Password invalid.");

        if (!keepGoing) {
            return;
        }

        sendText("Enter ppsn: ");
        Message ppsnMessage = readMessage();
        String ppsn = ppsnMessage.message();
        System.out.println("PPSN: " + ppsn);
        keepGoing = replyToUser(ppsnOk(ppsnMessage.message()), "PPSN Okay.", "PPSN invalid.");

        if (!keepGoing) {
            return;
        }

        sendText("Enter height: ");
        Message heightMessage = readMessage();
        System.out.println("Height: " + heightMessage.message());
        keepGoing = replyToUser(doubleOkay(heightMessage.message()), "Height Okay.", "Height invalid.");

        if (!keepGoing) {
            return;
        }

        double height = Double.parseDouble(heightMessage.message());


        sendText("Enter weight: ");
        Message weightMessage = readMessage();
        System.out.println("Weight: " + weightMessage.message());
        keepGoing = replyToUser(doubleOkay(weightMessage.message()), "Weight Okay.", "Weight invalid.");

        if (!keepGoing) {
            return;
        }

        double weight = Double.parseDouble(weightMessage.message());

        sendText("Enter age: ");
        Message ageMessage = readMessage();
        System.out.println("Age: " + ageMessage.message());
        keepGoing = replyToUser(doubleOkay(ageMessage.message()), "Age Okay.", "Age invalid.");

        if (!keepGoing) {
            return;
        }

        int age = Integer.parseInt(ageMessage.message());

        final User user = new User(userName, passHash, ppsn, height, weight, age);
//        System.out.println(user);

        try {
            System.out.println("Adding user: " + user);
            db.addUser(user);
            System.out.println("Successfully added user.");
            sendCode(Code.OK);
        } catch (IOException e) {
            System.out.println("Error adding user.");
            sendCode(Code.BAD);
        }
    }
}
