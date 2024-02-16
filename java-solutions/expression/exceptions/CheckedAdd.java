package expression.exceptions;

import expression.Add;
import expression.MyExpression;

public class CheckedAdd extends Add {
    public CheckedAdd(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        int res = super.evaluateOperation(valueL, valueR);
        if (valueL >= 0 && valueR >= 0 && res < 0 || valueL < 0 && valueR < 0 && res >= 0) {
            throw new OverflowException(String.format("Overflow at addition %d + %d", valueL, valueR));
        }
        return res;
    }
}