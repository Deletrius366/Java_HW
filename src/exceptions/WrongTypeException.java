package exceptions;

public class WrongTypeException extends Exception {
    public WrongTypeException() {
        super("Unknown type for evaluating");
    }
}
