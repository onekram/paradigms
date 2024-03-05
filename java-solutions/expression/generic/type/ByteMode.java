package expression.generic.type;

public class ByteMode implements Mode<Byte> {
    @Override
    public Byte getFromInt(int value) {
        return (byte) value;
    }

    @Override
    public Byte multiply(Byte v1, Byte v2) {
        return (byte)(v1 * v2);
    }

    @Override
    public Byte divide(Byte v1, Byte v2) {
        return (byte)(v1 / v2);
    }

    @Override
    public Byte add(Byte v1, Byte v2) {
        return (byte)(v1 + v2);
    }

    @Override
    public Byte subtract(Byte v1, Byte v2) {
        return (byte)(v1 - v2);
    }

    @Override
    public Byte negate(Byte v1) {
        return (byte)(-v1);
    }

    @Override
    public Byte min(Byte v1, Byte v2) {
        return (byte) Math.min(v1, v2);
    }

    @Override
    public Byte max(Byte v1, Byte v2) {
        return (byte) Math.max(v1, v2);
    }

    @Override
    public Byte count(Byte v1) {
        if (v1 < 0) {
            return (byte) (Integer.bitCount(v1) - 24);
        }
        return (byte) (Integer.bitCount(v1));
    }
}
