package cz.cvut.fel.memorice.model.util;

/**
 * Created by sheemon on 18.3.16.
 */
public class InvalidNameException extends WrongNameException {

    public InvalidNameException() {
        super("The set contains invalid name");
    }
}
