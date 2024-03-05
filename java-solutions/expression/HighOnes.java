package expression;

import java.math.BigInteger;

public class HighOnes extends UnaryOperation {
    public HighOnes (MyExpression expression) {
        super(expression);
    }

    @Override
    protected int evaluateImpl(int value) {
        long mask = 1;
        int count = 0;
        while (Integer.MAX_VALUE * 2L >= mask) {
            count++;
            if ((value & mask) == 0) {
                count = 0;
            }
            mask <<= 1;
        }
        return count;
    }
    @Override
    protected BigInteger evaluateImpl(BigInteger valueL) {
        return valueL.negate();
    }

    @Override
    public String getSign() {
        return "l1";
    }
}
