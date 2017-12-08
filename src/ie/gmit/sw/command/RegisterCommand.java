package ie.gmit.sw.command;

import ie.gmit.sw.User;
import ie.gmit.sw.databases.Database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RegisterCommand extends DatabaseCommand {

    public RegisterCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db) {
        super(objIn, objOut, db);
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


    @Override
    public void execute() {

        String userName = sendAndReceive("Enter your new username: ");
//        System.out.println("Username: " + userName);
        sendMessage(nameOk(userName) ? "OK" : "BAD");

        String password = sendAndReceive("Enter your password: ");
//        System.out.println("Password: " + password);
        sendMessage(passwordOk(password) ? "OK" : "BAD");

        String ppsn = sendAndReceive("Enter ppsn:");
//        System.out.println("ppsn: " + ppsn);
        sendMessage(ppsnOk(ppsn) ? "OK" : "BAD");

        String height = sendAndReceive("Enter height: ");
//        System.out.println("Height: " + height);
        sendMessage(doubleOkay(height) ? "OK" : "BAD");
        double heightAsDouble = Double.parseDouble(height);

        String weight = sendAndReceive("Enter weight: ");
//        System.out.println("Weight: " + weight);
        sendMessage(doubleOkay(weight) ? "OK" : "BAD");
        double weightAsDouble = Double.parseDouble(weight);

        String age = sendAndReceive("Enter age: ");
//        System.out.println("Age: " + age);
        sendMessage(intOkay(age) ? "OK" : "BAD");
        int ageAsInt = Integer.parseInt(age);

        final User user = new User(userName, password.hashCode(), ppsn, heightAsDouble, weightAsDouble, ageAsInt);

        try {
            System.out.println("Adding user: " + user);
            db.addUser(user);
            System.out.println("Successfully added user.");
            sendMessage("OK");
        } catch (IOException e) {
            System.out.println("Error adding user.");
            sendMessage("BAD");
        }
    }
}
