package expression.exceptions;

public class DivisionByZeroException extends EvaluateException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
