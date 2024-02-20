package expression;

import java.math.BigInteger;

public class LowOnes extends UnaryOperation {
    public LowOnes (MyExpression expression) {
        super(expression);
    }

    @Override
    protected int evaluateOperation(int value) {
        long mask = 1;
        int count = 0;
        while (Integer.MAX_VALUE * 2L >= mask && (value & mask) != 0) {
            count++;
            mask <<= 1;
        }
        return count;
    }
    @Override
    protected BigInteger evaluateOperation(BigInteger valueL) {
        return valueL.negate();
    }

    @Override
    public String getSign() {
        return "t1";
    }

}
