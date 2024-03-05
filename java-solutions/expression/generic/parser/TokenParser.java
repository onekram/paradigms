package expression.generic.parser;

import java.util.*;
import java.util.function.Predicate;

public class TokenParser {
    private int pos;
    private int lastPos;
    private String currentElement;
    private final String expression;
    private final HashSet<Character> openBrackets = new HashSet<>(Set.of('(', '[', '{'));
    private final HashSet<String> variables = new HashSet<>(Set.of("x", "y", "z"));
    private final Predicate<Integer> lettersToken;
    private final Predicate<Integer> digitsToken;

    public TokenParser(String expression) {
        this.expression = expression;
        lettersToken = i -> (i < expression.length()) && (Character.isLetter(expression.charAt(i)));
        digitsToken = i -> (i < expression.length()) && (Character.isDigit(expression.charAt(i)));
    }
    public boolean isEnd() {
        return pos >= expression.length();
    }

    public boolean parseVar() {
        StringBuilder sb = new StringBuilder();
        int currentPos = pos;
        while (lettersToken.test(currentPos)) {
            sb.append(expression.charAt(currentPos++));
        }
        if (variables.contains(sb.toString())) {
            currentElement = sb.toString();
            pos = currentPos;
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
                sb.append(expression.charAt(currentPos++));
            } while (digitsToken.test(currentPos));
        } else {
            while (digitsToken.test(currentPos)) {
                sb.append(expression.charAt(currentPos++));
            }
        }
        if (!sb.isEmpty()) {
            currentElement = sb.toString();
            pos = currentPos;
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
        Predicate<Character> condition;
        boolean flag = test(Character::isLetter, currentPos); // Parsing not a simple operation
        if (flag) {
            condition = ch -> Character.isDigit(ch) || Character.isLetter(ch);
        } else {
            condition = ch -> !Character.isWhitespace(ch) && !openBrackets.contains(ch) && !Character.isDigit(ch) && !Character.isLetter(ch);
        }
        while (currentPos < expression.length() && test(condition, currentPos)) {
            sb.append(expression.charAt(currentPos++));
            if (!flag && operations.containsKey(sb.toString())) {
                currentElement = sb.toString();
                pos = currentPos;
                return true;
            }
        }
        if (!flag) {
            return false;
        }
        if (operations.containsKey(sb.toString())) {
            currentElement = sb.toString();
            pos = currentPos;
            return true;
        }
        return false;
    }

    public String parseToken() {
        skipSpace();
        StringBuilder sb = new StringBuilder();
        int currentPos = pos;
        Predicate<Character> condition;
        if (test(Character::isLetter, currentPos)) {
            condition = ch -> Character.isDigit(ch) || Character.isLetter(ch);
        } else {
            condition = ch -> !Character.isWhitespace(ch) && !openBrackets.contains(ch) && !Character.isDigit(ch) && !Character.isLetter(ch);
        }
        while (currentPos < expression.length() && test(condition, currentPos)) {
            sb.append(expression.charAt(currentPos++));
        }
        return sb.toString();
    }
    public boolean test(Predicate<Character> condition, int currentPos) {
        return condition.test(expression.charAt(currentPos));
    }
    public boolean test(Predicate<Character> condition) {
        return condition.test(expression.charAt(pos));
    }

    public boolean test(char c) {
        return expression.charAt(pos) == c;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }
    public String getCurrentElement() {
        return currentElement;
    }
    private boolean checkNext() {
        return digitsToken.test(pos + 1);
    }
}
