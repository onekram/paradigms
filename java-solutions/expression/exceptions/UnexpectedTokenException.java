package expression.exceptions;

public class UnexpectedTokenException extends ParsingException {
    public UnexpectedTokenException(String message) {
        super(message);
    }

    public UnexpectedTokenException(String message, int pos) {
        super(message, pos);
    }
}
