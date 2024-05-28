package expression.parser;

import expression.*;
import expression.exceptions.ListParser;
import expression.exceptions.ParsingException;
import expression.exceptions.TripleParser;
import expression.exceptions.UnexpectedTokenException;


import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class ExpressionParser implements ListParser, TripleParser {
    protected TokenParser tokenParser;

    private final Map<String, BiOp> binaryOperations;
    private final Map<String, UnOp> unaryOperations;
    private final List<Integer> priorityList;

    public ExpressionParser() {
        this(
                Map.of(
                        "+", new BiOp(Add::new, 10),
                        "-", new BiOp(Subtract::new, 10),
                        "*", new BiOp(Multiply::new, 20),
                        "/", new BiOp(Divide::new, 20),
                        "min", new BiOp(Min::new, 5),
                        "max", new BiOp(Max::new, 5),
                        "<<", new BiOp(LShift::new, 1),
                        ">>", new BiOp(RShift::new, 1),
                        ">>>", new BiOp(AShift::new, 1)

                ),
                Map.of(
                        "-", new UnOp(Negate::new)
                )
        );
    }

    public ExpressionParser(Map<String, BiOp> binaryOperations, Map<String, UnOp> unaryOperations) {
        this.binaryOperations = binaryOperations;
        this.unaryOperations = unaryOperations;
        priorityList = new HashSet<>(
                        binaryOperations
                                .values()
                                .stream()
                                .map(BiOp::getPriority)
                                .toList())
                .stream()
                .sorted()
                .toList();
    }

    public MyExpression parse(String expression) throws ParsingException {
        return parse(expression, List.of("x", "y", "z"));
    }

    public MyExpression parse(String expression, List<String> variables) throws ParsingException {
        this.tokenParser = new TokenParser(expression.trim(), variables);
        MyExpression ex = expression();
        if (!tokenParser.isEnd()) {
            throw new UnexpectedTokenException("Unexpected end of expression");
        }
        return ex;
    }
    private MyExpression expression() throws ParsingException {
        return expression(0);
    }

    private MyExpression expression(int priorityIndex) throws ParsingException {
        int currentPriority = priorityList.get(priorityIndex++);
        MyExpression first = priorityIndex == priorityList.size()
                ? factor()
                : expression(priorityIndex);
        while (tokenParser.hasNext()) {
            String element;
            tokenParser.skipSpace();
            if (tokenParser.tesCloseBracket()) {
                return first;
            }
            int currentPos = tokenParser.getPos();
            if (tokenParser.parseToken(binaryOperations)) {
                element = tokenParser.getToken();
            } else {
                throw new UnexpectedTokenException(String.format("Unexpected token '%s'", tokenParser.parseToken()),
                        tokenParser.getPos());
            }
            if (binaryOperations.get(element).getPriority() != currentPriority) {
                tokenParser.setNewToken();
                tokenParser.setPos(currentPos);
                return first;
            }
            MyExpression second = priorityIndex == priorityList.size()
                    ? factor()
                    : expression(priorityIndex);
            first = binaryOperations.get(element).apply(first, second);
        }
        return first;
    }

    private MyExpression factor() throws ParsingException {
        tokenParser.skipSpace();
        if (tokenParser.testOpenBracket()) {
            char open = tokenParser.getCur();
            char close = tokenParser.getPaired(open);

            tokenParser.step();
            MyExpression result = expression();
            if (tokenParser.isEnd()) {
                throw new UnexpectedTokenException("Expected " + close);
            }
            if (!tokenParser.test(close)) {
                throw new ParsingException("Expected " + close + " but found " + tokenParser.parseToken());
            }
            tokenParser.step();
            return result;
        }
        if (tokenParser.isEnd()) {
            throw new UnexpectedTokenException("Unexpected end of expression");
        }
        if (tokenParser.parseVar()) {
            String varToken = tokenParser.getToken();
            return new Variable(varToken, tokenParser.getVarIndex(varToken));
        } else if (tokenParser.parseConst()) {
            return new Const(parseInt(tokenParser.getToken()));
        } else if (tokenParser.parseToken(unaryOperations)) {
            return unaryOperations.get(tokenParser.getToken()).apply(factor());
        } else {
            throw new UnexpectedTokenException(String.format("Unexpected token '%s'", tokenParser.parseToken()),
                    tokenParser.getPos());
        }
    }

    protected int parseInt(String value) throws ParsingException {
        return Integer.parseInt(value);
    }

    public static class BiOp {
        private final BinaryOperator<MyExpression> func;
        private final int priority;

        public BiOp(BinaryOperator<MyExpression> func, int priority) {
            this.priority = priority;
            this.func = func;
        }
        public MyExpression apply(MyExpression left, MyExpression right) {
            return func.apply(left, right);
        }
        public int getPriority() {
            return priority;
        }
    }

    public static class UnOp {
        private final UnaryOperator<MyExpression> func;

        public UnOp(UnaryOperator<MyExpression> func) {
            this.func = func;
        }
        public MyExpression apply(MyExpression left) {
            return func.apply(left);
        }
    }
}
