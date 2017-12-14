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
            Log.error("Error reading users from database. ERROR: " + e);
            return new ArrayList<>();
        }
    }

    @Override
    public void execute() {
        Log.info("Login attempt beginning");

        sendMessage("Enter Username: ");
        final String userName = readMessage().message();
        Log.info("User name: " + userName);
        sendMessage("Enter password: ");
        final String password = readMessage().message();
        Log.info("Password: " + password);

        final List<User> users = getUsers();

        final boolean userExists = users.stream()
                .filter(user -> user.getUserName().equals(userName)) // find user with this name.
                .filter(user -> user.getPassHash() == password.hashCode()) // find users with this password
                .count() > 0; // should be exactly 1 user with this user/pass combination.

        if (userExists) {
            sendMessage("Successfully logged in!", Code.OK);
            Log.info("Client logged in with valid credentials.");
            client.login(userName.hashCode()); // update state of Client object to logged in.
            return;
        }

        Log.warning(String.format("Username: [%s] and Password: [%s] Did not match any users.", userName, password));
        sendMessage("Username and password don't match any existing user.", Code.BAD);
    }
}
