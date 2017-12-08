package ie.gmit.sw;

import ie.gmit.sw.databases.DatabaseItem;

public class User implements DatabaseItem {

    private final int id;
    private final String userName;
    private final int passHash;
    private final String ppsn;
    private final double height;
    private final double weight;
    private final int age;

    public User(String userName, int passHash, String ppsn, double height, double weight, int age) {
        this.id = userName.hashCode();
        this.userName = userName;
        this.passHash = passHash;
        this.ppsn = ppsn;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }


    public User(final String[] args) {
        this(
                args[1],
                Integer.parseInt(args[2]),
                args[3],
                Double.parseDouble(args[4]),
                Double.parseDouble(args[5]),
                Integer.parseInt(args[6])
        );
    }

    public String toString() {
        return id + ";" + userName + ";" + passHash + ";" + ppsn + ";" + height + ";" + weight + ";" + age;
    }

    public String getUserName() {
        return userName;
    }

    public int getPassHash() {
        return passHash;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        return !(other == null || !(other instanceof User)) && id == ((User) other).id;
    }

    @Override
    public String toDatabaseFormat() {
        return id + ";" + userName + ";" + passHash + ";" + ppsn + ";" + height + ";" + weight + ";" + age;
    }
}
