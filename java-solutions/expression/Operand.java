package expression;

public class Operand {
    private Operation type = Operation.INIT;
    private String name = "default";
    public Operand(Operation type) {
        this.type = type;
        this.name = type.getName();
    }

    public Operand(Operation type, String name) {
        this.type = type;
        this.name = name;
    }

    public static Operand getConst (String name) {
        return new Operand(Operation.CONST, name);
    }

    public Operand(String value) {
        name = value;
        switch (value) {
            case "+":
                type = Operation.PLUS;
                break;
            case "-":
                type = Operation.MINUS;
                break;
            case "*":
                type = Operation.MUL;
                break;
            case "/":
                type = Operation.DIV;
                break;
            case "(":
                type = Operation.LEFT_PARENTHESES;
                break;
            case "[":
                type = Operation.LEFT_BRACKET;
                break;
            case "{":
                type = Operation.LEFT_BRACE;
                break;
            case ")":
                type = Operation.RIGHT_PARENTHESES;
                break;
            case "]":
                type = Operation.RIGHT_BRACKET;
                break;
            case "}":
                type = Operation.RIGHT_BRACE;
                break;
            case "&":
                type = Operation.AND;
                break;
            case "|":
                type = Operation.OR;
                break;
            case "^":
                type = Operation.XOR;
                break;
            case "l1":
                type = Operation.UNARY_L1;
                break;
            case "t1":
                type = Operation.UNARY_T1;
                break;
            case "unary minus":
                type = Operation.UNARY_MINUS;
                break;
            case "min":
                type = Operation.MIN;
                break;
            case "max":
                type = Operation.MAX;
                break;
            case "<<":
                type = Operation.L_SHIFT;
                break;
            case ">>":
                type = Operation.R_SHIFT;
                break;
            case ">>>":
                type = Operation.A_SHIFT;
                break;
            default:
                type = Operation.UNKNOWN;
        }
    }
    public Operation getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name;
    }
}
