package expression;

import java.math.BigInteger;

public class Add extends BinaryOperation {

    public Add(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateImpl(int valueL, int valueR) {
        return valueL + valueR;
    }
    @Override
    protected BigInteger evaluateImpl(BigInteger valueL, BigInteger valueR) {
        return valueL.add(valueR);
    }
    @Override
    protected String getSign() {
        return "+";
    }
    @Override
    public int getPriority() {
        return 10;
    }
    protected boolean canOpen(MyExpression expression) {
        return true;
    }
}
