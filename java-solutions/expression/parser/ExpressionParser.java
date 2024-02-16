package expression.parser;

import expression.*;
import expression.exceptions.ListParser;
import expression.exceptions.UnexpectedTokenException;

import java.util.List;

public class ExpressionParser implements ListParser {
    protected TokenParser runner;
    private final int start = 6;
    public MyExpression parse(String expression) {
        this.runner = new TokenParser(expression, List.of("x", "y", "z"));
        runner.nextStep();
        MyExpression ex = expression(start);
        if (!runner.isEnd()) {
            throw new UnexpectedTokenException("Unexpected end of expression");
        }
        return ex;
    }

    @Override
    public MyExpression parse(String expression, List<String> variables) {
        this.runner = new TokenParser(expression, variables);
        runner.nextStep();
        MyExpression ex = expression(start);
        if (!runner.isEnd()) {
            throw new UnexpectedTokenException("Unexpected end of expression");
        }
        return ex;
    }

    private MyExpression expression(int priority) {
        MyExpression first = parseOperand(priority);

        while (runner.hasNext()) {
            Operand operator = runner.getCurrentElement();
            if (operator.getType() == Operation.UNKNOWN) {
                throw new UnexpectedTokenException(String.format("Unknown operation '%s'", operator),
                        runner.getPos());
            }
            if (operator.getType().compare(Priority.getPriority(priority))) {
                break;
            } else {
                runner.nextStep();
            }

            MyExpression second = parseOperand(priority);
            first = action(first, second, operator);
        }
        return first;
    }
    MyExpression parseOperand(int priority) {
        return priority == 1 ? factor() : expression(priority - 1);
    }
    protected MyExpression action(MyExpression first, MyExpression second, Operand operand) {
        return switch (operand.getType()) {
            case MINUS -> new Subtract(first, second);
            case PLUS -> new Add(first, second);
            case MUL -> new Multiply(first, second);
            case DIV -> new Divide(first, second);
            case OR -> new Or(first, second);
            case AND -> new And(first, second);
            case XOR -> new Xor(first, second);
            case MIN -> new Min(first, second);
            case MAX -> new Max(first, second);
            case L_SHIFT -> new LShift(first, second);
            case R_SHIFT -> new RShift(first, second);
            case A_SHIFT -> new AShift(first, second);
            default -> throw new IllegalArgumentException(String.format("No action expected for '%s'", operand));
        };
    }

    protected MyExpression factor() {
        Operand next = runner.getCurrentElement();
        if (next.getType().isBinary()) {
            throw new UnexpectedTokenException(String.format("Unexpected token '%s'", next), runner.getPos());
        }
        MyExpression result;
        if (runner.isOpenBracket(next.getType())) {
            runner.nextStep();
            result = expression(start);
            if (!runner.isPairedBracket(next.getType(), runner.getCurrentElement().getType())) {
                throw new UnexpectedTokenException(String.format("Expected ) but found '%s'",
                        runner.getCurrentElement()),
                        runner.getPos());
            }
            runner.nextStep();
            return result;
        }

        runner.nextStep();
        return parseStep(next);
    }

    protected MyExpression parseStep(Operand next) {
        return switch (next.getType()) {
            case UNARY_MINUS -> new Negate(factor());
            case CONST -> new Const(Integer.parseInt(next.getName()));
            case UNARY_L1 -> new HighOnes(factor());
            case UNARY_T1 -> new LowOnes(factor());
            case VAR -> new Variable(next.getName(), runner.getVariablesIndex(next.getName()));
            default -> throw new UnexpectedTokenException(String.format("Unexpected token '%s'", next),
                    runner.getPos());
        };
    }

}

