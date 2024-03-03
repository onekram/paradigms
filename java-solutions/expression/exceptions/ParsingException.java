package expression.exceptions;

public class ParsingException extends Exception {
    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, int pos, Throwable cause) {
        super(format(message, pos), cause);
    }

    public ParsingException(String message, int pos) {
        super(format(message, pos));
    }
    private static String format(String message, int pos) {
        return message + " at position: " + pos;
    }
}
