package expression.exceptions;

import expression.Multiply;
import expression.MyExpression;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }
    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        int res = super.evaluateOperation(valueL, valueR);
        if (valueR != 0 && valueL != res / valueR || valueL == Integer.MIN_VALUE && valueR == -1) {
            throw new OverflowException(String.format("Overflow at multiplication %d * %d", valueL, valueR));
        }
        return res;
    }
}