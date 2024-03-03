package expression;

import java.math.BigInteger;

public class Add extends BinaryOperation {

    public Add(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        return valueL + valueR;
    }
    @Override
    protected BigInteger evaluateOperation(BigInteger valueL, BigInteger valueR) {
        return valueL.add(valueR);
    }
    @Override
    protected String getSign() {
        return "+";
    }
    @Override
    public Priority getPriority() {
        return Priority.COMMON;
    }
    protected boolean canOpen(MyExpression expression) {
        return true;
    }
}
