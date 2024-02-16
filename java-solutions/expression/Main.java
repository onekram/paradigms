package expression;


import expression.exceptions.ExpressionParser;

public class Main {
    public static void main(String[] args) {
        ExpressionParser expressionParser = new ExpressionParser();
        MyExpression ex = expressionParser.parse("((y + z) - 1)");
        System.out.println(ex.toMiniString());
    }

}