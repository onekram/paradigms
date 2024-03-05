package expression.generic.type;

import java.util.Map;

public class IntegerMode implements Mode<Integer> {
    @Override
    public Integer getFromInt(int value) {
        return value;
    }

    @Override
    public Integer multiply(Integer v1, Integer v2) {
        return v1 * v2;
    }

    @Override
    public Integer divide(Integer v1, Integer v2) {
        return v1 / v2;
    }

    @Override
    public Integer add(Integer v1, Integer v2) {
        return v1 + v2;
    }

    @Override
    public Integer subtract(Integer v1, Integer v2) {
        return v1 - v2;
    }

    @Override
    public Integer negate(Integer v1) {
        return -v1;
    }

    @Override
    public Integer min(Integer v1, Integer v2) {
        return Math.min(v1, v2);
    }

    @Override
    public Integer max(Integer v1, Integer v2) {
        return Math.max(v1, v2);
    }

    @Override
    public Integer count(Integer v1) {
        return Integer.bitCount(v1);
    }
}
