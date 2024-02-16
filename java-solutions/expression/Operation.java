package expression;

import java.util.Set;

public enum Operation {
    MINUS("-"),
    UNARY_MINUS("-"),
    PLUS("+"),
    MUL("*"),
    DIV("/"),
    LEFT_PARENTHESES("("),
    LEFT_BRACKET("["),
    LEFT_BRACE("{"),
    RIGHT_PARENTHESES(")"),
    RIGHT_BRACKET("]"),
    RIGHT_BRACE("}"),
    OR("|"),
    AND("&"),
    XOR("^"),
    CONST("number"),
    VAR("variable"),
    UNARY_L1("unary l1"),
    UNARY_T1("unary t1"),
    INIT("start expression"),
    END("end expression"),
    MIN("min"),
    MAX("max"),
    R_SHIFT("<<"),
    L_SHIFT(">>"),
    A_SHIFT(">>>"),
    UNKNOWN("unknown");

    private final String name;

    public String getName() {
        return name;
    }

    Operation (String name) {
        this.name = name;
    }
    public boolean isBinary() {
        return Set.of(MINUS, PLUS, MUL, DIV, OR, AND, XOR, MAX, MIN).contains(this);
    }

    public boolean compare(Priority priority) {
        return switch (priority) {
            case HIGH ->  this != MUL && this != DIV;
            case COMMON -> this != PLUS && this != MINUS;
            case AND -> this != AND;
            case XOR -> this != XOR;
            case OR -> this != OR && this != DIV;
            case LOW -> this != MIN && this != MAX && this != R_SHIFT && this != L_SHIFT && this != A_SHIFT;
            default -> throw new IllegalArgumentException();
        };
    }
}
