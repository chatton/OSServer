package ie.gmit.sw.command;

import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.server.Client;
import ie.gmit.sw.server.User;
import ie.gmit.sw.databases.Database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginCommand extends DatabaseCommand {

    public LoginCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }

    @Override
    public void execute() {
        sendText("Enter Username: ");
        String userName = readMessage().message();
        System.out.println("User name: " + userName);
        sendText("Enter password: ");
        String password = readMessage().message();
        System.out.println("Password: " + password);

        List<User> users = new ArrayList<>();
        try {
            users = db.getUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }


//
        for (User user : users) {
            System.out.println("USER: " + userName);
            if (user.getPassHash() == password.hashCode()
                    && user.getUserName().equals(userName)) {
                System.out.println("Sending OK");
                sendMessage(new Message("ok", Code.OK));
                System.out.println("Client logged in with valid credentials!");
                client.login();
                return;
            }
        }
        sendMessage(new Message("bad", Code.BAD));
//        System.out.println("Sending BAD");
//        sendMessage("BAD");
    }
}
