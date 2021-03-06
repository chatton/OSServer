package ie.gmit.sw.databases;

import ie.gmit.sw.logging.Log;
import ie.gmit.sw.server.User;
import ie.gmit.sw.records.FitnessRecord;
import ie.gmit.sw.records.MealRecord;
import ie.gmit.sw.records.Record;
import ie.gmit.sw.records.RecordType;

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

    public Database() throws IOException {
        this("data", "data/users.dat", "data/user_records/");
    }

    public Database(final String dataPath, final String usersPath, final String recordsPath) throws IOException {
        this.usersPath = usersPath;
        this.recordsPath = recordsPath;
        sep = ";";
        createDirIfAbsent(dataPath);
        createFileIfAbsent(usersPath);
        createDirIfAbsent(recordsPath);
    }

    private void createFileIfAbsent(String path) throws IOException {
        synchronized (this) {
            final File file = new File(path);
            if (!file.exists()) {
                final boolean createdFile = file.createNewFile();
                if (createdFile) {
                    Log.info("Successfully created file [" + path + "]");
                } else {
                    Log.error("Error creating file [" + path + "]");
                }
            }
        }
    }

    private void createDirIfAbsent(final String path) throws IOException {
        synchronized (this) {
            final File file = new File(path);
            if (!file.exists()) {
                final boolean createdDir = file.mkdir();
                if (createdDir) {
                    Log.info("Successfully created directory [" + path + "]");
                } else {
                    Log.error("Error creating directory [" + path + "]");
                }
            }
        }
    }

    /*
    Given a user id, returns all of the records for that specific user.
     */
    private List<Record> getRecordsForUser(final int userId) throws IOException {
        synchronized (this) {
            final File recordFile = getUserFile(userId);
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
    private <T extends Record> List<T> getRecordsOfTypeFor(final int userId, final RecordType type) throws IOException {
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

    public List<FitnessRecord> getFitnessRecords(int userId) throws IOException {
        return getRecordsOfTypeFor(userId, RecordType.FITNESS);
    }

    public List<MealRecord> getMealRecords(int userId) throws IOException {
        return getRecordsOfTypeFor(userId, RecordType.MEAL);
    }

    /*
    In order to delete a record, the records are read, one is removed from the list
    in memory, and then they are re-saved.
     */
    private void overwriteRecords(final List<Record> records, final int userId) throws IOException {
        synchronized (this) {
            final File recordFile = getUserFile(userId);
            final boolean deleted = recordFile.delete();
            if (!deleted) {
                throw new IOException("Failed deleting file [" + recordFile + "]");
            }
            for (final Record record : records) { // save records.
                saveRecord(record);
            }
        }
    }

    /*
    Gets the file corresponding to a given user id. May or may not exist.
     */
    private File getUserFile(final int userId) {
        return new File(recordsPath + userId + ".dat");
    }

    /*
    In order to save a record, The file is created if it doesn't already exist.
    Then a line is appended to the file which represents the new Record to be added.
     */
    private boolean saveRecord(final Record record) throws IOException {
        synchronized (this) {
            final File recordFile = getUserFile(record.getUserId());

            if (!recordFile.exists()) {
                if (!recordFile.createNewFile()) {
                    throw new IOException("Error creating file for user: " + record.getUserId() + ".dat");
                }
            }

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
        return getUsers().stream().noneMatch(user -> user.getUserName().equals(name));
    }

    /*
    Generates a unique to that user ID.
    */
    private int getNextRecordId(int userId) throws IOException {
        final List<Record> records = getRecordsForUser(userId); // get the users existing records.

        // Get their ids as a set
        final Set<Integer> ids = records.stream()
                .map(Record::getId)
                .collect(Collectors.toSet());

        return IntStream.iterate(0, i -> i + 1)
                .filter(num -> !ids.contains(num)) // want an id that doesn't exist already
                .findFirst() // first one will do
                .orElse(records.size()); // Should never reach here as the int stream isn't limited.
    }

    public void addUser(final User user) throws IOException {
        add(user, usersPath);
    }

    public boolean addRecord(final Record record) throws IOException {
        record.setId(getNextRecordId(record.getUserId())); // assign a unique ID to the record before saving.
        return saveRecord(record);
    }
}
