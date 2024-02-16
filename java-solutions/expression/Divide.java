package expression;

import java.math.BigInteger;

public class Divide extends BinaryOperation {

    public Divide(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        return valueL / valueR;
    }
    @Override
    protected BigInteger evaluateOperation(BigInteger valueL, BigInteger valueR) {
        return valueL.divide(valueR);
    }


    @Override
    protected String getSign() {
        return "/";
    }
    @Override
    public Priority getPriority() {
        return Priority.HIGH;
    }
    @Override
    protected boolean canOpen(MyExpression expression) {
        return false;
    }
}
