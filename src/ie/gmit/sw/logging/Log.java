package ie.gmit.sw.logging;

import java.util.Calendar;

/*
Simply logging class to display different levels of logging information.
 */
public class Log {

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private static boolean debugEnabled = false;

    public static void toggleDebugMode() {
        debugEnabled = !debugEnabled;
    }

    private Log(){} // prevent instantiation

    public static void info(String text) {
        displayLog("[INFO]", ANSI_GREEN, text);
    }

    public static void error(String text) {
        displayLog("[ERROR]", ANSI_RED, text);
    }

    public static void warning(String text) {
        displayLog("[WARNING]", ANSI_YELLOW, text);
    }

    public static void debug(String text) {
        if (debugEnabled) {
            displayLog("[DEBUG]", ANSI_CYAN, text);
        }
    }

    private static void displayLog(String level, String ansiColour, String text) {
        System.out.println(ansiColour + level + " " + text + " - " + Calendar.getInstance().getTime() + ANSI_RESET);
    }
}
