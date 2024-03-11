package expression.generic.type;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

public class SatMode implements Mode<Integer> {
    @Override
    public Integer getFromInt(int value) {
        return value;
    }
    @Override
    public Integer getFromString(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer multiply(Integer v1, Integer v2) {
        if (v1 > 0) {
            if (v2 > 0 && Integer.MAX_VALUE / v2 < v1) {
                return Integer.MAX_VALUE;
            }
            if (v2 < 0 && (Integer.MIN_VALUE / v2 < v1 && !v2.equals(-1))) {
                return Integer.MIN_VALUE;
            }
        }
        if  (v1 < 0) {
            if (v2 > 0 && Integer.MIN_VALUE / v2 > v1) {
                return Integer.MIN_VALUE;
            }
            if (v2 < 0 && Integer.MAX_VALUE / v2 > v1){
                return Integer.MAX_VALUE;
            }
        }
        return v1 * v2;
    }

    @Override
    public Integer divide(Integer v1, Integer v2) {
        if (v2.equals(0)) {
            throw new DivisionByZeroException("Division by zero");
        }
        if (v1.equals(Integer.MIN_VALUE) && v2.equals(-1)) {
            return Integer.MAX_VALUE;
        }
        return v1 / v2;
    }

    @Override
    public Integer add(Integer v1, Integer v2) {
        if (v1 > 0 && v2 > 0 && v1 > Integer.MAX_VALUE - v2) {
            return Integer.MAX_VALUE;
        }
        if (v1 < 0 && v2 < 0 && v1 < Integer.MIN_VALUE - v2) {
            return Integer.MIN_VALUE;
        }
        return v1 + v2;
    }

    @Override
    public Integer subtract(Integer v1, Integer v2) {
        if (v1 >= 0 && v2 < 0 && v1 > Integer.MAX_VALUE + v2) {
            return Integer.MAX_VALUE;
        }
        if (v1 < 0 && v2 > 0 && v1 < Integer.MIN_VALUE + v2) {
            return Integer.MIN_VALUE;
        }
        return v1 - v2;
    }

    @Override
    public Integer negate(Integer v1) {
        if (v1 == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        }
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
