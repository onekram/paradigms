package expression.generic.parser;


import expression.exceptions.ParsingException;
import expression.exceptions.UnexpectedTokenException;
import expression.generic.operations.*;


import java.util.*;

public class ExpressionParser<T extends Number> {
    private TokenParser tokenParser;
    private final Map<String, BinaryOperation<T>> binaryOperations;
    private final Map<String, UnaryOperation<T>> unaryOperations;
    private final List<Integer> priorityList;

    public ExpressionParser(Map<String, BinaryOperation<T>> binaryOperation,
                            Map<String, UnaryOperation<T>> unaryOperations) {
        this.binaryOperations = binaryOperation;
        this.unaryOperations = unaryOperations;
        priorityList = binaryOperation.values()
                .stream()
                .map(BinaryOperation::getPriority)
                .sorted(Comparator.reverseOrder())
                .toList();
    }


    public Expression<T> parse(String expression) throws ParsingException {
        this.tokenParser = new TokenParser(expression);
        Expression<T> ex = expression(priorityList.iterator());
        if (!tokenParser.isEnd()) {
            throw new UnexpectedTokenException("Unexpected end of expression");
        }
        return ex;
    }

    private Expression<T> expression(Iterator<Integer> priorityIterator) throws ParsingException {
        int currentPriority = priorityIterator.next();
        Expression<T> first = !priorityIterator.hasNext() ? factor() : expression(priorityIterator);
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
            if (binaryOperations.get(element).getPriority() != currentPriority) {
                tokenParser.setPos(currentPos);
                return first;
            }
            Expression<T> second = !priorityIterator.hasNext() ? factor() : expression(priorityIterator);
            first = binaryOperations.get(element).apply(first, second);
        }
        return first;
    }

    public Expression<T> factor() throws ParsingException {
        tokenParser.skipSpace();
        if (tokenParser.test('(')) {
            tokenParser.step();
            Expression<T> result = expression(priorityList.iterator());
            if (tokenParser.isEnd()) {
                throw new UnexpectedTokenException("Expected )");
            }
            if (!tokenParser.take(')')) {
                throw new ParsingException("Expected ) but found " + tokenParser.parseToken());
            }
            return result;
        }
        if (tokenParser.isEnd()) {
            throw new UnexpectedTokenException("Unexpected end of expression");
        }
        if (tokenParser.parseVar()) {
            return new Variable<>(tokenParser.getToken());
        } else if (tokenParser.parseConst()) {
            return new Const<>(tokenParser.getToken());
        } else if (tokenParser.parseToken(unaryOperations)) {
            return unaryOperations.get(tokenParser.getToken()).apply(factor());
        } else {
            throw new UnexpectedTokenException(String.format("Unexpected token '%s'", tokenParser.parseToken()),
                    tokenParser.getPos());
        }
    }
}
