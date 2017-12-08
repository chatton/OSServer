package ie.gmit.sw;

import ie.gmit.sw.databases.Database;
import ie.gmit.sw.records.FitnessRecord;
import ie.gmit.sw.records.MealRecord;
import ie.gmit.sw.records.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerRunner {
    public static void main(String[] args) throws IOException {
//        Server server = new Server(9999);
//        server.start();
//        System.out.println("Exiting...");
        Database db = new Database();
        Record r = new MealRecord(1, 5, "Some description");
        Record r2 = new FitnessRecord(2, 5, "mode", 2.4);
        db.addRecord(r2);
        db.addRecord(r);
        System.out.println(db.getFitnessRecordsForUser(456));
        System.out.println(db.getMealRecordsForUser(456));
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

