package expression.generic.operations;

import expression.generic.type.Mode;

public class Variable <T extends Number> implements Expression<T> {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public T evaluate(Mode<T> mode, T v1, T v2, T v3) {
        return switch (name) {
            case "x" -> v1;
            case "y" -> v2;
            case "z" -> v3;
            default -> throw new AssertionError("Error: Invalid name of variable: " + name);
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
