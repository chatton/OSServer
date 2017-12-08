package ie.gmit.sw.serialize;

import java.io.Serializable;

import static ie.gmit.sw.serialize.Code.OK;

public class Message implements Serializable {

    private static final long serialVersionUID = 42L;
    private final String message;
    private final Code code;

    public Message(String message, Code code) {
        this.message = message;
        this.code = code;
    }

    public String message() {
        return message;
    }

    public Code code() {
        return code;
    }

    public boolean ok() {
        return code == OK;
    }
}
