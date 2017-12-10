package ie.gmit.sw.records;

public class FitnessRecord extends Record {
    private double duration;
    private String mode;

    public FitnessRecord(int id, int userId, String mode, double duration) {
        super(id, userId, RecordType.FITNESS);
        this.duration = duration;
        this.mode = mode;
    }

    public FitnessRecord(int userId, String mode, double duration) {
        super(userId, RecordType.FITNESS);
        this.duration = duration;
        this.mode = mode;
    }

    public FitnessRecord(String[] args) {
        this(
                Integer.parseInt(args[0]), // record id
                Integer.parseInt(args[2]), // user id
                args[3], // mode
                Double.parseDouble(args[4]) // duration
        );
    }

    public String getMode() {
        return mode;
    }


    public double getDuration() {
        return duration;
    }

    @Override
    public String toDatabaseFormat() {
        return String.format("%d;%s;%d;%s;%f",
                getId(),
                getType().toString().toUpperCase(),
                getUserId(),
                mode,
                duration
        );
    }
}
