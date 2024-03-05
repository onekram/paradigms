package expression;

import java.math.BigInteger;

public class Negate extends UnaryOperation {
    public Negate(MyExpression expression) {
        super(expression);
    }

    @Override
    protected int evaluateImpl(int value) {
        return -value;
    }
    @Override
    protected BigInteger evaluateImpl(BigInteger valueL) {
        return valueL.negate();
    }

    @Override
    public String getSign() {
        return "-";
    }
}
