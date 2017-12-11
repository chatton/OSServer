package ie.gmit.sw.databases;

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
import java.util.stream.Stream;

public class Database {

    private final String usersPath;
    private final String recordsPath;
    private final String sep;

    public Database(final String usersPath, final String recordsPath) {
        this.usersPath = usersPath;
        this.recordsPath = recordsPath;
        this.sep = ";";
    }

    public Database() {
        this("data/users.dat", "data/user_records/");
    }

    private List<Record> getRecordsForUser(int userId) throws IOException {
        synchronized (this) {
            final File recordFile = new File(recordsPath + userId);
            if (recordFile.exists()) {
                try (final Stream<String> lines = Files.lines(Paths.get(recordFile.getPath()))) {
                    return lines.map(line -> line.split(sep))
                            .map(this::makeRecord)
                            .collect(Collectors.toList());
                }
            }
            return new ArrayList<>(); // no records found for user
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Record> List<T> getRecordsOfTypeFor(int userId, RecordType type) throws IOException {
        return getRecordsForUser(userId)
                .stream()
                .filter(record -> record.getType() == type)
                .map(record -> (T) record) // safe to cast based on type being the same
                .collect(Collectors.toList());
    }

    public List<MealRecord> getMealRecordsForUser(int userId) throws IOException {
        return getRecordsOfTypeFor(userId, RecordType.MEAL);
    }

    public List<FitnessRecord> getFitnessRecordsForUser(int userId) throws IOException {
        return getRecordsOfTypeFor(userId, RecordType.FITNESS);
    }

    public boolean deleteRecord(int userId, int recordId) throws IOException {
        final List<Record> records = getRecordsForUser(userId);
        final boolean removed = records.removeIf(record -> record.getId() == recordId);
        if (removed) { // only overwrite if there was actually something changed.
            overwriteRecords(records, userId);
        }
        return removed;
    }

    public List<FitnessRecord> getFitnessRecords(int userId) throws IOException {
        return getFitnessRecords(userId, 10);
    }

    public List<FitnessRecord> getFitnessRecords(int userId, int nLast) throws IOException {
        return nLast(nLast, getFitnessRecordsForUser(userId));
    }

    public List<MealRecord> getMealRecords(int userId) throws IOException {
        return getMealRecords(userId, 10);
    }

    public List<MealRecord> getMealRecords(int userId, int nLast) throws IOException {
        return nLast(nLast, getMealRecordsForUser(userId));
    }

    private <T extends Record> List<T> nLast(int nLast, List<T> records) throws IOException {
        int startingPos = records.size() - nLast;
        startingPos = startingPos < 0 ? 0 : startingPos;
        return records.stream()
                .skip(startingPos) // skip all but the last n
                .collect(Collectors.toList()); // return as a list.
    }


    @SuppressWarnings("all")
    private void overwriteRecords(List<Record> records, int userId) throws IOException {
        synchronized (this) {
            final File recordFile = new File("data/user_records/" + userId);
            recordFile.delete(); // method call for side effect, not using return value.
            for (final Record record : records) { // save all but the deleted record.
                saveRecord(record);
            }
        }
    }


    private void saveRecord(Record record) throws IOException {
        synchronized (this) {
            final File recordFile = new File("data/user_records/" + record.getUserId());

            if (!recordFile.exists()) {
                if (!recordFile.createNewFile()) {
                    throw new IOException("Error creating file for user: " + record.getUserId());
                }
            }

            try (FileWriter fw = new FileWriter(recordFile, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(record.toDatabaseFormat() + System.lineSeparator());
            }
        }
    }

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

    public boolean nameAvailable(String name) throws IOException {
        synchronized (this) {
            return getUsers().stream().noneMatch(user -> user.getUserName().equals(name));
        }
    }

    @SuppressWarnings("all")
    public int getNextRecordId(int userId) throws IOException {
        final List<Record> records = getRecordsForUser(userId);
        final Set<Integer> ids = records.stream().map(Record::getId).collect(Collectors.toSet());
        for (int i = 0; i < records.size(); i++) {
            if (!ids.contains(i)) {
                return i;
            }
        }
        return records.size();
    }

    public void addUser(User user) throws IOException {
        add(user, usersPath);
    }

    public void addRecord(Record record) throws IOException {
        saveRecord(record);
    }
}
