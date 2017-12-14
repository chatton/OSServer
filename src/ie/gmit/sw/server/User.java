package ie.gmit.sw.server;

import ie.gmit.sw.databases.DatabaseItem;

public class User implements DatabaseItem {

    private final int id;
    private final String userName;
    private final String address;
    private final int passHash;
    private final String ppsn;
    private final double height;
    private final double weight;
    private final int age;

    public User(String userName, String address, int passHash, String ppsn, double height, double weight, int age) {
        this.id = userName.hashCode();
        this.userName = userName;
        this.address = address;
        this.passHash = passHash;
        this.ppsn = ppsn;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }


    public User(final String[] args) {
        this(
                args[1], // username,
                args[2], // address
                Integer.parseInt(args[3]), // password hash
                args[4], // ppsn
                Double.parseDouble(args[5]), // height
                Double.parseDouble(args[6]), // weight
                Integer.parseInt(args[7]) // age
        );
    }

    public String toString() {
        return "User{userName=" + userName + "}";
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
        return id + ";" + userName + ";" + address + ";" + passHash + ";" + ppsn + ";" + height + ";" + weight + ";" + age;
    }
}
