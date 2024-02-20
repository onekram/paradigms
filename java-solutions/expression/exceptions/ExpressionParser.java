package expression.exceptions;

import expression.*;

public class ExpressionParser extends expression.parser.ExpressionParser {
    @Override
    protected MyExpression action(MyExpression first, MyExpression second, Operand operand) {
        return switch (operand.getType()) {
            case MINUS -> new CheckedSubtract(first, second);
            case PLUS -> new CheckedAdd(first, second);
            case MUL -> new CheckedMultiply(first, second);
            case DIV -> new CheckedDivide(first, second);
            case OR -> new Or(first, second);
            case AND -> new And(first, second);
            case XOR -> new Xor(first, second);
            case MIN -> new Min(first, second);
            case MAX -> new Max(first, second);
            case L_SHIFT -> new LShift(first, second);
            case R_SHIFT -> new RShift(first, second);
            case A_SHIFT -> new AShift(first, second);
            default -> throw new RuntimeException(String.format("No action expected for %s", operand));
        };
    }

    @Override
    protected MyExpression parseStep(Operand next) {
        return switch (next.getType()) {
            case UNARY_MINUS -> new CheckedNegate(factor());
            case CONST -> new Const(parseInt(next.getName()));
            case UNARY_L1 -> new HighOnes(factor());
            case UNARY_T1 -> new LowOnes(factor());
            case VAR -> new Variable(next.getName(), runner.getVariablesIndex(next.getName()));
            default -> throw new UnexpectedTokenException(String.format("Unexpected operation '%s'", next),
                    runner.getPos());
        };
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new ConstOverflowException(String.format(value), runner.getPos(), ex);
        }
    }
}
