package expression.exceptions;

// :NOTE: RuntimeException?
public class ParsingException extends RuntimeException {
    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, int pos, Throwable cause) {
        super(format(message, pos), cause);
    }

    public ParsingException(String message, int pos) {
        super(format(message, pos));
    }

    public static String format(String message, int pos) {
        return message + " at position: " + pos;
    }
}
