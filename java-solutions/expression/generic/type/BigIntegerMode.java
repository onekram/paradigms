package expression.generic.type;

import java.math.BigInteger;

public class BigIntegerMode implements Mode<BigInteger> {
    @Override
    public BigInteger getFromInt(int value) {
        return BigInteger.valueOf(value);
    }
    @Override
    public BigInteger multiply(BigInteger v1, BigInteger v2) {
        return v1.multiply(v2);
    }

    @Override
    public BigInteger divide(BigInteger v1, BigInteger v2) {
        return v1.divide(v2);
    }

    @Override
    public BigInteger add(BigInteger v1, BigInteger v2) {
        return v1.add(v2);
    }

    @Override
    public BigInteger subtract(BigInteger v1, BigInteger v2) {
        return v1.subtract(v2);
    }

    @Override
    public BigInteger negate(BigInteger v1) {
        return v1.negate();
    }
}

