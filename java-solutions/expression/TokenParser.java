package expression;

import java.util.*;

public class TokenParser {
    private int pos;

    private Operand currentElement;
    private final String expression;
    private final HashSet<Character> operations = new HashSet<>(Set.of('+', '*', '/', '&', '^', '|', '(', ')', '[', ']', '{', '}'));
    private final HashSet<Character> openBrackets = new HashSet<>(Set.of('(', '[', '{'));
    private final HashSet<Character> unaryOperations = new HashSet<>(Set.of('-', 'l', 't'));
    private final HashSet<String> variables;
    private final HashMap<String, Integer> dictVariables;

    private final HashMap<Operation, Operation> brackets = new HashMap<>(Map.of(
            Operation.LEFT_BRACE, Operation.RIGHT_BRACE,
            Operation.LEFT_BRACKET, Operation.RIGHT_BRACKET,
            Operation.LEFT_PARENTHESES, Operation.RIGHT_PARENTHESES));

    public TokenParser(String expression, List<String> variables) {
        this.variables = new HashSet<>(variables);
        this.dictVariables = new HashMap<>();
        for (int i = 0; i < variables.size(); i++) {
            this.dictVariables.put(variables.get(i), i);
        }
        this.expression = expression;
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
        if (pos >= expression.length()) {
            currentElement = new Operand(Operation.END);
            return;
        }
        char value = expression.charAt(pos);
        if (!parseVar()) {
            if (operations.contains(value)) {
                currentElement = new Operand(String.valueOf(value));
                pos++;
            }
            else if (value == 'm'){
                currentElement = new Operand(parseToken(true));
            } else if (value == '<' || value == '>') {
                currentElement = new Operand(parseToken(true));
            } else if (unaryOperations.contains(value)) {
                currentElement = parseMinus();
            } else {
                currentElement = Operand.getConst(parseInt());
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
        while (currentPos < expression.length() &&
                !Character.isWhitespace(expression.charAt(currentPos)) &&
                !operations.contains((expression.charAt(currentPos)))&&
                expression.charAt(currentPos) != '-')  {
            sb.append(expression.charAt(currentPos));
            currentPos++;
        }
        if (variables.contains(sb.toString())) {
            currentElement = new Operand(Operation.VAR, sb.toString());
            pos = currentPos;
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
    private Operand parseMinus() {
        if (expression.charAt(pos) == '-') {
            if (isBinaryMinus()) {
                pos++;
                return new Operand("-");
            } else if (checkNext()) {
                return Operand.getConst(parseInt());
            } else {
                pos++;
                return new Operand("unary minus");
            }
        } else {
            return new Operand(parseToken(false));
        }
    }

    private String  parseToken(boolean checkMinus) {
        StringBuilder sb = new StringBuilder();
        while (pos < expression.length() &&
                !Character.isWhitespace(expression.charAt(pos)) &&
                !openBrackets.contains(expression.charAt(pos)) &&
                expression.charAt(pos) != '+')  {
            if (checkMinus && expression.charAt(pos) == '-') {
                break;
            }
            sb.append(expression.charAt(pos));
            pos++;
        }
        return sb.toString();
    }

    private String parseInt() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(expression.charAt(pos));
            pos++;
        } while (pos < expression.length() && Character.isDigit(expression.charAt(pos)));
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
        int currentPos = pos + 1;
        if (currentPos < expression.length() && Character.isWhitespace(expression.charAt(currentPos))) {
            return false;
        }
        if (currentPos >= expression.length()) {
            return false;
        }
        return !operations.contains(expression.charAt(currentPos)) &&
                !Character.isLetter(expression.charAt(currentPos)) &&
                expression.charAt(currentPos) != '-' &&
                expression.charAt(currentPos) != '$';
    }

    public int getPos() {
        return pos;
    }
}
