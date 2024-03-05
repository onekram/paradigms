package expression.generic.parser;

import java.util.*;
import java.util.function.Predicate;

public class TokenParser {

    private final char EOE = '\0';
    private int pos;
    private String token;
    private final String expression;
    private final HashSet<Character> openBrackets = new HashSet<>(Set.of('(', '[', '{'));
    private final HashSet<String> variables = new HashSet<>(Set.of("x", "y", "z"));
    private final Predicate<Integer> lettersToken;
    private final Predicate<Integer> digitsToken;
    private final Predicate<Integer> simpleToken;

    public TokenParser(String expression) {
        this.expression = expression;
        lettersToken = i -> i < expression.length() && Character.isLetter(getChar(i));
        digitsToken = i -> i < expression.length() && Character.isDigit(getChar(i));
        simpleToken = i -> i < expression.length() &&
                !Character.isWhitespace(getChar(i)) &&
                !openBrackets.contains(getChar(i)) &&
                !Character.isDigit(getChar(i)) &&
                !Character.isLetter(getChar(i));
    }

    public boolean isEnd() {
        return pos >= expression.length();
    }

    private char getChar(int i) {
        if (i >= expression.length()) {
            return EOE;
        }
        return expression.charAt(i);
    }

    public boolean parseVar() {
        StringBuilder sb = new StringBuilder();
        int currentPos = pos;
        while (lettersToken.test(currentPos)) {
            sb.append(getChar(currentPos++));
        }
        if (variables.contains(sb.toString())) {
            setCurrentToken(sb.toString(), currentPos);
            return true;
        }
        return false;
    }

    public boolean parseConst() {
        StringBuilder sb = new StringBuilder();
        int currentPos = pos;
        if (test('-')) {
            if (!checkNext()) {
                return false;
            }
            do {
                sb.append(getChar(currentPos++));
            } while (digitsToken.test(currentPos));
        } else {
            while (digitsToken.test(currentPos)) {
                sb.append(getChar(currentPos++));
            }
        }
        if (!sb.isEmpty()) {
            setCurrentToken(sb.toString(), currentPos);
            return true;
        }
        return false;
    }

    public void skipSpace() {
        while (pos < expression.length() && test(Character::isWhitespace)) {
            pos++;
        }
    }

    public boolean hasNext() {
        return pos < expression.length();
    }

    public void step() {
        pos++;
    }

    public <S> boolean parseToken(Map<String, S> operations) {
        skipSpace();
        StringBuilder sb = new StringBuilder();
        int currentPos = pos;
        Predicate<Integer> condition;
        boolean flag = test(Character::isLetter, currentPos); // Parsing not a simple operation
        if (flag) {
            condition = lettersToken.or(digitsToken);
        } else {
            condition = simpleToken;
        }
        while (condition.test(currentPos)) {
            sb.append(getChar(currentPos++));
            if (!flag && operations.containsKey(sb.toString())) {
                setCurrentToken(sb.toString(), currentPos);
                return true;
            }
        }
        if (!flag) {
            return false;
        }
        if (operations.containsKey(sb.toString())) {
            setCurrentToken(sb.toString(), currentPos);
            return true;
        }
        return false;
    }

    private void setCurrentToken(String token, int pos) {
        this.token = token;
        this.pos = pos;
    }

    public String parseToken() {
        skipSpace();
        StringBuilder sb = new StringBuilder();
        int currentPos = pos;
        Predicate<Integer> condition;
        if (test(Character::isLetter, currentPos)) {
            condition = lettersToken.or(digitsToken);
        } else {
            condition = simpleToken;
        }
        do {
            sb.append(getChar(currentPos++));
        } while (condition.test(currentPos));
        return sb.toString();
    }

    public boolean test(Predicate<Character> condition, int currentPos) {
        return condition.test(getChar(currentPos));
    }

    public boolean test(Predicate<Character> condition) {
        return condition.test(getChar(pos));
    }

    public boolean test(char c) {
        return getChar(pos) == c;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public String getToken() {
        return token;
    }

    private boolean checkNext() {
        return digitsToken.test(pos + 1);
    }
}
