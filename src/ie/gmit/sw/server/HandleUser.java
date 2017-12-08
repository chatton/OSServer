package ie.gmit.sw.server;

import ie.gmit.sw.serialize.Code;
import ie.gmit.sw.serialize.Message;
import ie.gmit.sw.command.Command;
import ie.gmit.sw.command.LoginCommand;
import ie.gmit.sw.command.MenuCommand;
import ie.gmit.sw.command.RegisterCommand;
import ie.gmit.sw.databases.Database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static ie.gmit.sw.serialize.Code.BAD;

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

    public Message readMessage() {
        try {
            return (Message) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
            return new Message("unable to read message", BAD);
        }
    }

    @Override
    public void run() {
        final Client client = new Client();

        while (true) {
            final Message choice = readMessage();
            if (choice.code() == BAD) { // socket disconnected.
                System.out.println("User disconnected.");
                return;
            }

            System.out.println("User sent: " + choice);
            Command cmd = null;
            switch (choice.code()) {
                case MENU:
                    System.out.println("Executing MENU cmd.");
                    cmd = new MenuCommand(objIn, objOut, client);
                    break;
                case REGISTER:
                    System.out.println("Executing REGISTER cmd.");
                    cmd = new RegisterCommand(objIn, objOut, db, client);
                    break;
                case LOGIN:
                    System.out.println("Executing LOGIN cmd.");
                    cmd = new LoginCommand(objIn, objOut, db, client);
                    break;
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
