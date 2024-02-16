package expression;

import java.math.BigInteger;

public class And extends BinaryOperation {

    public And(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        return valueL & valueR;
    }
    @Override
    protected BigInteger evaluateOperation(BigInteger valueL, BigInteger valueR) {
        return valueL.and(valueR);
    }

    @Override
    protected String getSign() {
        return "&";
    }
    @Override
    public Priority getPriority() {
        return Priority.AND;
    }
    @Override
    protected boolean canOpen(MyExpression expression) {
        return true;
    }
}

