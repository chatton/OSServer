package ie.gmit.sw.records;

import ie.gmit.sw.databases.DatabaseItem;

public abstract class Record implements DatabaseItem {

    private int id;
    private final int userId;
    private final RecordType type;

    Record(int id, int userId, RecordType type) {
        this.id = id;
        this.type = type;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public RecordType getType() {
        return type;
    }
}
