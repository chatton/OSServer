package ie.gmit.sw.databases;

import ie.gmit.sw.logging.Log;
import ie.gmit.sw.server.User;
import ie.gmit.sw.records.FitnessRecord;
import ie.gmit.sw.records.MealRecord;
import ie.gmit.sw.records.Record;
import ie.gmit.sw.records.RecordType;
import ie.gmit.sw.util.ListUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
The Database class represents the files storing the records and
user information on the Server. It provides an abstraction for
accessing and modifying the contents of the saved files.
 */
public class Database {

    private final String usersPath;
    private final String recordsPath;
    private final String sep;

    public Database(final String usersPath, final String recordsPath) throws IOException {
        this.usersPath = usersPath;
        this.recordsPath = recordsPath;
        this.sep = ";";
        createFileIfAbsent(usersPath);
        createDirIfAbsent(recordsPath);
    }

    @SuppressWarnings("all")
    private void createFileIfAbsent(String path) throws IOException {
        synchronized (this) {
            final File file = new File(path);
            if (!file.exists()) {
                Log.info("Creating file [" + path + "]");
                file.createNewFile();
            }
        }
    }

    @SuppressWarnings("all")
    private void createDirIfAbsent(String path) throws IOException {
        synchronized (this) {
            final File file = new File(path);
            if (!file.exists()) {
                Log.info("Creating directory [" + path + "]");
                file.mkdir();
            }
        }
    }

    public Database() throws IOException {
        this("data/users.dat", "data/user_records/");
    }

    /*
    Given a user id, returns all of the records for that specific user.
     */
    private List<Record> getRecordsForUser(int userId) throws IOException {
        synchronized (this) {
            final File recordFile = getUserRecordsFile(userId);
            if (recordFile.exists()) {
                try (final Stream<String> lines = Files.lines(Paths.get(recordFile.getPath()))) {
                    return lines.map(line -> line.split(sep)) // create array of args for each line
                            .map(this::makeRecord) // convert that into a Record object using the constructor that takes an array of strings
                            .collect(Collectors.toList()); // return it as a list.
                }
            }
            return new ArrayList<>(); // no records found for user
        }
    }

    /*
    Provide a way of extracting only records of a certain type.
     */
    @SuppressWarnings("unchecked")
    private <T extends Record> List<T> getRecordsOfTypeFor(int userId, RecordType type) throws IOException {
        return getRecordsForUser(userId)
                .stream()
                .filter(record -> record.getType() == type)
                .map(record -> (T) record) // safe to cast based on type being the same
                .collect(Collectors.toList());
    }


    /*
    Deleting a record requires a user ID as well as a record ID. Multiple users
    could have a record with the same id, so both are needed to identify.
     */
    public boolean deleteRecord(int userId, int recordId) throws IOException {
        final List<Record> records = getRecordsForUser(userId);
        final boolean removed = records.removeIf(record -> record.getId() == recordId);
        if (removed) { // only overwrite if there was actually something changed.
            overwriteRecords(records, userId); // re-save all the values with the deleted one gone.
        }
        return removed;
    }

    private List<MealRecord> getMealRecordsForUser(int userId) throws IOException {
        return getRecordsOfTypeFor(userId, RecordType.MEAL);
    }

    private List<FitnessRecord> getFitnessRecordsForUser(int userId) throws IOException {
        return getRecordsOfTypeFor(userId, RecordType.FITNESS);
    }

    public List<FitnessRecord> getFitnessRecords(int userId) throws IOException {
        return getFitnessRecords(userId, 10);
    }

    private List<FitnessRecord> getFitnessRecords(int userId, int nLast) throws IOException {
        return ListUtils.nLast(nLast, getFitnessRecordsForUser(userId));
    }

    public List<MealRecord> getMealRecords(int userId) throws IOException {
        return getMealRecords(userId, 10);
    }

    private List<MealRecord> getMealRecords(int userId, int nLast) throws IOException {
        return ListUtils.nLast(nLast, getMealRecordsForUser(userId));
    }

    /*
    In order to delete a record, the records are read, one is removed from the list
    in memeory, and then they are re-saved.
     */
    @SuppressWarnings("all")
    private void overwriteRecords(List<Record> records, int userId) throws IOException {
        synchronized (this) {
            final File recordFile = getUserFile(userId);
            recordFile.delete(); // method call for side effect, not using return value.
            for (final Record record : records) { // save records.
                saveRecord(record);
            }
        }
    }

    /*
    Gets the file corresponding to a given user id. May or may not exist.
     */
    private File getUserFile(int userId) {
        return new File("data/user_records/" + userId + ".dat");
    }

    /*
    Gets the file holding all the records of the user with the provided user id. May or may not exist.
     */
    private File getUserRecordsFile(int userId) {
        return new File("data/user_records/" + userId + ".dat");
    }

    /*
    In order to save a record, The file is created if it doesn't already exist.
    Then a line is appended to the file which represents the new Record to be added.
     */
    private boolean saveRecord(Record record) throws IOException {
        synchronized (this) {
            final File recordFile = getUserFile(record.getUserId());

            if (!recordFile.exists()) {
                if (!recordFile.createNewFile()) {
                    throw new IOException("Error creating file for user: " + record.getUserId() + ".dat");
                }
            }

            /*
            Try with resources syntax handles the closing of writers for me.
             */
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(recordFile, true))) {
                writer.write(record.toDatabaseFormat() + System.lineSeparator());
                return true;
            }
        }
    }

    /*
    Each record implementation has a constructor that takes an array of strings as args.
    This is used to serialize/de-serialize the records.
     */
    private Record makeRecord(final String[] args) {
        final RecordType type = RecordType.valueOf(args[1]);
        switch (type) {
            case FITNESS:
                return new FitnessRecord(args);
            case MEAL:
                return new MealRecord(args);
        }
        throw new IllegalArgumentException("Invalid RecordType provided. Args: " + Arrays.toString(args));
    }

    /*
    Returns a list of all the users that are registered with the system.
     */
    public List<User> getUsers() throws IOException {
        synchronized (this) {
            try (final Stream<String> lines = Files.lines(Paths.get(usersPath))) {
                return lines.map(line -> line.split(sep))
                        .map(User::new)
                        .collect(Collectors.toList());
            }
        }
    }

    private void add(DatabaseItem data, String path) throws IOException {
        synchronized (this) {
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
                writer.append(data.toDatabaseFormat()).append(System.lineSeparator());
            }
        }
    }

    /*
    returns true or false for if the provided name is available.
     */
    public boolean nameAvailable(final String name) throws IOException {
        synchronized (this) {
            return getUsers().stream().noneMatch(user -> user.getUserName().equals(name));
        }
    }

    /*
    Generates a unique to that user ID.
    */
    @SuppressWarnings("all")
    private int getNextRecordId(int userId) throws IOException {
        final List<Record> records = getRecordsForUser(userId); // get the users existing records.

        // Get their ids as a set
        final Set<Integer> ids = records.stream()
                .map(Record::getId)
                .collect(Collectors.toSet());

        return IntStream.iterate(0, i -> i + 1)
                .filter(num -> !ids.contains(num)) // want an id that doesn't exist already
                .findFirst() // first one will do
                .orElse(records.size()); // otherwise just give back the total number of records which will be unique if we've reached it.
    }

    public void addUser(final User user) throws IOException {
        add(user, usersPath);
    }

    public boolean addRecord(final Record record) throws IOException {
        record.setId(getNextRecordId(record.getUserId())); // assign a unique ID to the record before saving.
        return saveRecord(record);
    }
}
