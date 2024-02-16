package expression;

import expression.exceptions.EvaluateException;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public class Variable implements MyExpression {
    private final String varName;
    private final int varIndex;
    public Variable(String varName) {
        this.varName = varName;
        varIndex = -1;
    }
    public Variable(int varIndex) {
        this.varIndex = varIndex;
        varName = "NONE";
    }

    public Variable(String varName, int varIndex) {
        this.varIndex = varIndex;
        this.varName = varName;
    }

    @Override
    public int evaluate(int value) {
        if (varName.equals("x")) {
            return value;
        }
        throw new EvaluateException("Error: Invalid name of variable: " + varName);
    }

    @Override
    public int evaluate(int value1, int value2, int value3) {
        switch (varName) {
            case "x" -> {
                return value1;
            }
            case "y" -> {
                return value2;
            }
            case "z" -> {
                return value3;
            }
        }
        throw new EvaluateException("Error: Invalid name of variable: " + varName);
    }

    @Override
    public int evaluate(List<Integer> values) {
        if (varIndex == -1) {
            throw new EvaluateException("Error: Invalid index of variable: " + varName);
        } else {
            return values.get(varIndex);
        }
    }

    @Override
    public BigInteger evaluate(BigInteger value) {
        if (varName.equals("x")) {
            return value;
        }
        throw new EvaluateException("Error: Invalid name of variable: " + varName);
    }

    @Override
    public String toString() {
        return varName;
    }

    @Override
    public String toMiniString() {
        return this.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable other) {
            return Objects.equals(varName, other.varName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(varName);
    }

    @Override
    public Priority getPriority() {
        return Priority.NP;
    }
}
