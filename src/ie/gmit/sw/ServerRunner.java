package ie.gmit.sw;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.records.FitnessRecord;
import ie.gmit.sw.records.MealRecord;
import ie.gmit.sw.records.Record;
import ie.gmit.sw.server.Server;

import java.io.IOException;
import java.util.Scanner;

public class ServerRunner {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9090);
        server.start();

//        Scanner sc = new Scanner(System.in);
//        System.out.println("Exiting...");
//        Database db = new Database();
//        db.deleteRecord(5, 0);
//        Record r = new MealRecord(5, "Some description");
//        Record r2 = new FitnessRecord(5, "mode", 2.4);
//        db.addRecord(r2);
//        System.out.println("Added record 1.");
//        db.addRecord(r);
//        System.out.println("Added record 2.");
//        sc.nextLine();
//        System.out.println(db.getFitnessRecordsForUser(5));
//        System.out.println(db.getMealRecordsForUser(5));
//        System.out.println(db.getFitnessRecordsForUser(456));
//        System.out.println(db.getMealRecordsForUser(456));
//        db.deleteRecord(456, 122);
//        System.out.println(db.getRecordsForUser(456));
//        Collection<FitnessRecord> records = db.getFitnessRecords();
//        System.out.println(db.getFitnessRecords());
//        System.out.println(db.getLastNFitnessRecords(2));
//        System.out.println(db.getFitnessRecordsFor(1, 2));
//        System.out.println(db.getFitnessRecordsFor(2, 4));
//        db.deleteRecord(7);

    }

}

