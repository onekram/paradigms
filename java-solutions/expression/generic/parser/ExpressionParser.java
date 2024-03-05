package expression.generic.parser;


import expression.Priority;
import expression.exceptions.ParsingException;
import expression.exceptions.UnexpectedTokenException;
import expression.generic.operations.*;


import java.util.Map;

public class ExpressionParser<T extends Number> {
    private TokenParser tokenParser;
    private final Map<String, BinaryOperation<T>> binaryOperations;
    private final Map<String, UnaryOperation<T>> unaryOperations;

    private final int start = 6;

    public ExpressionParser(Map<String, BinaryOperation<T>> binaryOperation,
                            Map<String, UnaryOperation<T>> unaryOperations) {
        this.binaryOperations = binaryOperation;
        this.unaryOperations = unaryOperations;
    }


    public Expression<T> parse(String expression) throws ParsingException {
        this.tokenParser = new TokenParser(expression);
        Expression<T> ex = expression(start);
        if (!tokenParser.isEnd()) {
            throw new UnexpectedTokenException("Unexpected end of expression");
        }
        return ex;
    }

    private Expression<T> expression(int priority) throws ParsingException {
        Expression<T> first = priority == 1 ? factor() : expression(priority - 1);
        while (tokenParser.hasNext()) {
            String element;
            if (tokenParser.test(')')) {
                return first;
            }
            int currentPos = tokenParser.getPos();
            if (tokenParser.parseToken(binaryOperations)) {
                element = tokenParser.getToken();
            } else {
                throw new UnexpectedTokenException(String.format("Unexpected token '%s'", tokenParser.parseToken()),
                        tokenParser.getPos());
            }
            if (binaryOperations.get(element).getPriority() != Priority.getPriority(priority)) {
                tokenParser.setPos(currentPos);
                return first;
            }
            Expression<T> second = priority == 1 ? factor() : expression(priority - 1);
            first = binaryOperations.get(element).apply(first, second);
        }
        return first;
    }
    public Expression<T> factor() throws ParsingException {
        tokenParser.skipSpace();
        if (tokenParser.test('(')) {
            tokenParser.step();
            Expression<T> result = expression(start);
            if (tokenParser.isEnd()) {
                throw new UnexpectedTokenException("Expected )");
            }
            if (!tokenParser.test(')')) {
                throw new ParsingException("Expected ) but found " + tokenParser.parseToken());
            }
            tokenParser.step();
            return result;
        }
        if (tokenParser.isEnd()) {
            throw new UnexpectedTokenException("Unexpected end of expression");
        }
        if (tokenParser.parseVar()) {
            return new Variable<>(tokenParser.getToken());
        } else if (tokenParser.parseConst()) {
            return new Const<>(Integer.parseInt(tokenParser.getToken()));
        } else if (tokenParser.parseToken(unaryOperations)) {
            return unaryOperations.get(tokenParser.getToken()).apply(factor());
        } else {
            throw new UnexpectedTokenException(String.format("Unexpected token '%s'", tokenParser.parseToken()),
                    tokenParser.getPos());
        }
    }
}
