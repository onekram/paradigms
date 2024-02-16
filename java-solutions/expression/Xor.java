package expression;

import java.math.BigInteger;

public class Xor extends BinaryOperation {

    public Xor(MyExpression expressionL, MyExpression expressionR) {
        super(expressionL, expressionR);
    }

    @Override
    protected int evaluateOperation(int valueL, int valueR) {
        return valueL ^ valueR;
    }
    @Override
    protected BigInteger evaluateOperation(BigInteger valueL, BigInteger valueR) {
        return valueL.xor(valueR);
    }

    @Override
    protected String getSign() {
        return "^";
    }
    @Override
    public Priority getPriority() {
        return Priority.XOR;
    }
    @Override
    protected boolean canOpen(MyExpression expression) {
        return true;
    }
}

