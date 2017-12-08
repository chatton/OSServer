package ie.gmit.sw;

import ie.gmit.sw.command.Command;
import ie.gmit.sw.command.MenuCommand;
import ie.gmit.sw.command.RegisterCommand;
import ie.gmit.sw.databases.Database;
import ie.gmit.sw.records.Record;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HandleUser implements Runnable {

    private final ObjectOutputStream objOut;
    private final ObjectInputStream objIn;
    private final Database db;
    private boolean loggedIn = false;

    public HandleUser(final Socket socket, final Database db) throws IOException {
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objIn = new ObjectInputStream(socket.getInputStream());
        this.db = db;
    }


    public void addRecord(final Record record) {
        synchronized (db) {
            try {
                db.addRecord(record);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public void deleteRecord(int recordId) {
//        synchronized (db) {
//            try {
//                db.deleteRecord(recordId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private void handleLogin() {
//        System.out.println("Sending: Enter username");
//        String userName = sendMessage("Enter username: ");
//        System.out.println("Sending Enter password.");
//        String password = sendMessage("Enter password: ");
//        System.out.println("User entered name=" + userName + " password=" + password);
//        sendOk();
//    }

//    private String sendOk() {
//        return sendMessage(StatusCodes.OK);
//    }
//
//    private String sendMenu() {
//        return sendMessage("What do you want to do?\n1: Login\n2:Do stuff\n3. Even more stuff.");
//    }

    public void sendMessage(String message) {
        try {
            objOut.writeObject(message);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage() {
        try {
            return (String) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<User> getUsers() {
        synchronized (db) {
            try {
                return db.getUsers();
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

//    private boolean finished(String input) {
//        return input.equals(StatusCodes.EXIT);
//    }


    private boolean promptForLogin() {
        sendMessage("Enter Username: ");
        String userName = readMessage();
        System.out.println("User name: " + userName);
        sendMessage("Enter password: ");
        String password = readMessage();
        System.out.println("Password: " + password);

        for (User user : getUsers()) {
            System.out.println("USER: " + userName);
            if (user.getPassHash() == password.hashCode()
                    && user.getUserName().equals(userName)) {
                System.out.println("Sending OK");
                sendMessage("OK");
                return true;
            }

        }
        System.out.println("Sending BAD");
        sendMessage("BAD");
        return false;
    }


    @Override
    public void run() {

        while (true) {
            String choice = readMessage();
            System.out.println("User sent: " + choice);
            Command cmd = null;
            switch (choice) {
                case "MENU":
                    cmd = new MenuCommand(objIn, objOut);
                    break;
                case "REGISTER":
                    cmd = new RegisterCommand(objIn, objOut, db);
            }

            cmd.execute();
        }


//        displayMenu();

//        System.out.println("Choice made: " + choice);
//        boolean loggedIn = false;
//        while (!loggedIn) {
//            loggedIn = promptForLogin();
//        }
//        System.out.println("Logged in!");

//        boolean userLoggedIn = ;
//
//        while (!userLoggedIn) {
//
//        }


        // read in prompts, make a command, add it to the queue.
        // eg, read in all the promptForLogin details, add LoginCommand, which goes and creates a new user or something

        // read in Delete account code,

//        sendMessage(StatusCodes.OK); // indicate successful promptForLogin.
//
//        String userChoice = "";
//        while (!finished(userChoice)) {
//            userChoice = readMessage();
//            sendMessage("You just sent me: " + userChoice + " (enter 900 to quit)");
//        }
//        System.out.println("From User: " + userChoice);
//        switch (userChoice) {
//            case RequestCodes.LOGIN:
//                System.out.println("User wants to log in.");
//                handleLogin();
//                break;
//            case RequestCodes.MENU:
//                System.out.println("Display menu.");
//        }
    }

//    public String sendAndReceive(final String message) {
//        sendMessage(message);
//        return readMessage();
//    }


//


}
