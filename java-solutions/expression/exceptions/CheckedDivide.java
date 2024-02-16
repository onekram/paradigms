package expression.exceptions;

import expression.Divide;
import expression.MyExpression;

public class CheckedDivide extends Divide {
    public CheckedDivide(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }
    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        int res = super.evaluateOperation(valueL, valueR);
        if (valueR == 0) {
            throw new DivisionByZeroException("Division by zero");
        }
        if (valueL == Integer.MIN_VALUE && valueR == -1) {
            throw new OverflowException(String.format("Overflow at division %d / %d", valueL, valueR));
        }
        return res;
    }
}