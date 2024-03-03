package expression;


import expression.exceptions.ExpressionParser;
import expression.exceptions.ParsingException;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ExpressionParser expressionParser = new ExpressionParser();
        try {
            MyExpression ex = expressionParser.parse("5min5");
            System.out.println(ex.toMiniString());
            System.out.println(ex.getClass());
        } catch (ParsingException e) {
            System.out.println("Error parsing expression: " + e.getMessage());
        }
    }
}
