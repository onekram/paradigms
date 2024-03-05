package expression.generic.type;


public class DoubleMode implements Mode<Double> {
    @Override
    public Double getFromInt(int value) {
        return (double) value;
    }

    @Override
    public Double multiply(Double v1, Double v2) {
        return v1 * v2;
    }

    @Override
    public Double divide(Double v1, Double v2) {
        return v1 / v2;
    }

    @Override
    public Double add(Double v1, Double v2) {
        return v1 + v2;
    }

    @Override
    public Double subtract(Double v1, Double v2) {
        return v1 - v2;
    }

    @Override
    public Double negate(Double v1) {
        return -v1;
    }

    @Override
    public Double min(Double v1, Double v2) {
        return Math.min(v1, v2);
    }

    @Override
    public Double max(Double v1, Double v2) {
        return Math.max(v1, v2);
    }

    @Override
    public Double count(Double v1) {
        return (double) Long.bitCount(Double.doubleToLongBits(v1));
    }
}
