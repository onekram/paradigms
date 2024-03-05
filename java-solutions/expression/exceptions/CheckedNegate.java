package expression.exceptions;

import expression.MyExpression;
import expression.Negate;

public class CheckedNegate extends Negate {
    public CheckedNegate(MyExpression expression) {
        super(expression);
    }

    @Override
    protected int evaluateImpl(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException(String.format("Overflow at negation %d", value));
        }
        return super.evaluateImpl(value);
    }
}
