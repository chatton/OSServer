package ie.gmit.sw.records;

public class MealRecord extends Record {

    private final String desc;

    public MealRecord(int id, int userId, String desc) {
        super(id, userId, RecordType.MEAL);
        if (desc.length() > 100) {
            throw new IllegalArgumentException("Description can have a maximum of 100 characters.");
        }
        this.desc = desc;
    }

    public MealRecord(String[] args) {
        this(
                Integer.parseInt(args[0]), // record id
                Integer.parseInt(args[2]), // user id
                args[3] // description
        );
    }

//    public String getDesc() {
//        return desc;
//    }

    @Override
    public String toDatabaseFormat() {
        return String.format("%d;%s;%d;%s",
                getId(),
                getType().toString().toUpperCase(),
                getUserId(),
                desc
        );
    }
}
