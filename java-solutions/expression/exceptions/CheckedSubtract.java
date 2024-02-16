package expression.exceptions;

import expression.MyExpression;
import expression.Subtract;

public class CheckedSubtract extends Subtract {
    public CheckedSubtract(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }
    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        int res = super.evaluateOperation(valueL, valueR);
        if (valueL >= 0 && valueR < 0 && res < 0 || valueL < 0 && valueR >= 0 && res >= 0) {
            throw new OverflowException(String.format("Overflow at subtract %d - %d", valueL, valueR));
        }
        return res;
    }
}