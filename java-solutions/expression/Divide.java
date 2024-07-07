package expression;

import java.math.BigInteger;

public class Divide extends BinaryOperation {

    public Divide(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateImpl(int valueL, int valueR) {
        return valueL / valueR;
    }
    @Override
    protected BigInteger evaluateImpl(BigInteger valueL, BigInteger valueR) {
        return valueL.divide(valueR);
    }


    @Override
    protected String getSign() {
        return "/";
    }
    @Override
    public int getPriority() {
        return 20;
    }
    @Override
    protected boolean canOpen(MyExpression expression) {
        return false;
    }
}
