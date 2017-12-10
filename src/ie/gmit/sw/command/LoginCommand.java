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
import java.util.Collections;
import java.util.List;

public class LoginCommand extends DatabaseCommand {

    public LoginCommand(ObjectInputStream objIn, ObjectOutputStream objOut, Database db, Client client) {
        super(objIn, objOut, db, client);
    }


    private List<User> getUsers() {
        try {
            return Collections.unmodifiableList(db.getUsers());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void execute() {
        sendText("Enter Username: ");
        String userName = readMessage().message();
        System.out.println("User name: " + userName);
        sendText("Enter password: ");
        String password = readMessage().message();
        System.out.println("Password: " + password);

        final List<User> users = getUsers();

        final boolean userExists = users.stream()
                .filter(user -> user.getUserName().equals(userName)) // find user with this name.
                .filter(user -> user.getPassHash() == password.hashCode()) // find users with this password
                .count() > 0; // should be exactly 1 user with this user/pass combination.

        if (userExists) {
            sendMessage(new Message("Successfully logged in!", Code.OK));
            System.out.println("Client logged in with valid credentials!");
            client.login(userName.hashCode()); // update state of Client object to logged in.
            return;
        }

        sendMessage(new Message("Username and password don't match any existing user.", Code.BAD));
    }
}
