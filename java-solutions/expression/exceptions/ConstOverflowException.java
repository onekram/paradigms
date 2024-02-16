package expression.exceptions;

public class ConstOverflowException extends ParsingException{
    public ConstOverflowException(String message, int pos, Throwable cause) {
        super("Overflow while parsing " + message, pos, cause);
    }
}
