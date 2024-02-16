package expression.exceptions;

import expression.MyExpression;
import expression.Negate;

public class CheckedNegate extends Negate {
    public CheckedNegate(MyExpression expression) {
        super(expression);
    }

    @Override
    protected int evaluateOperation(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException(String.format("Overflow at negation %d", value));
        }
        return super.evaluateOperation(value);
    }
}
