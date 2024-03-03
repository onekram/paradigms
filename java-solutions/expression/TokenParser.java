package expression;

import java.util.*;
import java.util.function.Predicate;

public class TokenParser {
    private int pos;
    private int lastPos;
    private Operand currentElement;
    private final String expression;
    private final HashSet<Character> simpleOperations = new HashSet<>(
            Set.of('+', '*', '/', '&', '^', '|', '(', ')', '[', ']', '{', '}'));
    private final HashSet<Character> openBrackets = new HashSet<>(Set.of('(', '[', '{'));
    private final HashSet<String> variables;
    private final HashMap<String, Integer> dictVariables;
    private final HashMap<Operation, Operation> brackets = new HashMap<>(Map.of(
            Operation.LEFT_BRACE, Operation.RIGHT_BRACE,
            Operation.LEFT_BRACKET, Operation.RIGHT_BRACKET,
            Operation.LEFT_PARENTHESES, Operation.RIGHT_PARENTHESES));

    private final Predicate<Integer> lettersToken;
    private final Predicate<Integer> digitsToken;

    public TokenParser(String expression, List<String> variables) {
        this.variables = new HashSet<>(variables);
        this.dictVariables = new HashMap<>();
        for (int i = 0; i < variables.size(); i++) {
            this.dictVariables.put(variables.get(i), i);
        }
        this.expression = expression;
        lettersToken = i -> (i < expression.length()) && (Character.isLetter(expression.charAt(i)));
        digitsToken = i -> (i < expression.length()) && (Character.isDigit(expression.charAt(i)));
        this.currentElement = new Operand(Operation.INIT);
    }
    public String getExpression() {
        return expression;
    }
    public int getVariablesIndex(String varName) {
        return dictVariables.get(varName);
    }
    public boolean isEnd() {
        return currentElement.getType() == Operation.END;
    }

    public void nextStep() {
        skipSpace();
        lastPos = pos;
        if (pos >= expression.length()) {
            currentElement = new Operand(Operation.END);
            return;
        }
        char value = expression.charAt(pos);
        if (!parseSimple() && !parseVar() && !parseConst()) {
            if (value == '-') {
                pos++;
                if (isBinaryMinus()) {
                    currentElement = new Operand("-");
                } else {
                    currentElement = Operand.getUnaryMinus();
                }
            } else {
                 currentElement = new Operand(parseToken());
            }
        }
    }
    public boolean isOpenBracket(Operation type) {
        return List.of(Operation.LEFT_BRACE, Operation.LEFT_BRACKET, Operation.LEFT_PARENTHESES).contains(type);
    }

    public boolean isCloseBracket(Operation type) {
        return List.of(Operation.RIGHT_BRACE, Operation.RIGHT_BRACKET, Operation.RIGHT_PARENTHESES).contains(type);
    }
    public boolean isPairedBracket(Operation open, Operation close) {
        return brackets.get(open).equals(close);
    }

    private boolean parseVar() {
        StringBuilder sb = new StringBuilder();
        int currentPos = pos;
        if (expression.charAt(currentPos) == '$') {
            do {
                sb.append(expression.charAt(currentPos));
                currentPos++;
            } while (digitsToken.test(currentPos));
        } else {
            while (lettersToken.test(currentPos)) {
                sb.append(expression.charAt(currentPos));
                currentPos++;
            }
        }
        if (variables.contains(sb.toString())) {
            currentElement = new Operand(Operation.VAR, sb.toString());
            pos = currentPos;
            return true;
        }
        return false;
    }
    private boolean parseConst() {
        StringBuilder sb = new StringBuilder();
        int currentPos = pos;
        if (expression.charAt(currentPos) == '-') {
            if (isBinaryMinus() || !checkNext()) {
                return false;
            }
            do {
                sb.append(expression.charAt(currentPos));
                currentPos++;
            } while (digitsToken.test(currentPos));
        } else {
            while (digitsToken.test(currentPos)) {
                sb.append(expression.charAt(currentPos));
                currentPos++;
            }
        }
        if (!sb.isEmpty()) {
            currentElement = Operand.getConst(sb.toString());
            pos = currentPos;
            return true;
        }
        return false;
    }
    private boolean parseSimple() {
        char value = expression.charAt(pos);
        if (simpleOperations.contains(value)){
            pos++;
            currentElement = new Operand(String.valueOf(value));
            return true;
        }
        return false;
    }
    private void skipSpace() {
        while (pos < expression.length() && Character.isWhitespace(expression.charAt(pos))) {
            pos++;
        }
    }
    public boolean hasNext() {
        return pos < expression.length();
    }
    private String parseToken() {
        StringBuilder sb = new StringBuilder();
        while (pos < expression.length() &&
                !Character.isWhitespace(expression.charAt(pos)) &&
                !openBrackets.contains(expression.charAt(pos)) &&
                expression.charAt(pos) != '-') {
            sb.append(expression.charAt(pos));
            pos++;
        }
        return sb.toString();
    }
    private boolean isBinaryMinus() {
        return currentElement.getType() == Operation.CONST ||
                isCloseBracket(currentElement.getType()) ||
                currentElement.getType() == Operation.VAR;
    }
    public Operand getCurrentElement() {
        return currentElement;
    }
    private boolean checkNext() {
        return digitsToken.test(pos + 1);
    }
    public int getPos() {
        return lastPos;
    }
}
