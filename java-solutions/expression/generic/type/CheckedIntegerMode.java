package expression.generic.type;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

public class CheckedIntegerMode implements Mode<Integer> {
    @Override
    public Integer getFromInt(int value) {
        return value;
    }

    @Override
    public Integer multiply(Integer v1, Integer v2) {
        int res = v1 * v2;
        if (v2 != 0 && v1 != res / v2 || v1 == Integer.MIN_VALUE && v2 == -1) {
            throw new OverflowException(String.format("Overflow at multiplication %d * %d", v1, v2));
        }
        return res;
    }

    @Override
    public Integer divide(Integer v1, Integer v2) {
        if (v2 == 0) {
            throw new DivisionByZeroException("Division by zero");
        }
        if (v1 == Integer.MIN_VALUE && v2 == -1) {
            throw new OverflowException(String.format("Overflow at division %d / %d", v1, v2));
        }
        return v1 / v2;
    }

    @Override
    public Integer add(Integer v1, Integer v2) {
        int res = v1 + v2;
        if (v1 >= 0 && v2 >= 0 && res < 0 || v1 < 0 && v2 < 0 && res >= 0) {
            throw new OverflowException(String.format("Overflow at addition %d + %d", v1, v1));
        }
        return res;
    }

    @Override
    public Integer subtract(Integer v1, Integer v2) {
        int res = v1 - v2;
        if (v1 >= 0 && v2 < 0 && res < 0 || v1 < 0 && v2 >= 0 && res >= 0) {
            throw new OverflowException(String.format("Overflow at subtract %d - %d", v1, v2));
        }
        return res;
    }

    @Override
    public Integer negate(Integer v1) {
        if (v1 == Integer.MIN_VALUE) {
            throw new OverflowException(String.format("Overflow at negation %d", v1));
        }
        return -v1;
    }
}
