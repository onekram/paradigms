package expression.generic;

import expression.Priority;
import expression.exceptions.EvaluateException;
import expression.exceptions.ParsingException;
import expression.generic.operations.BinaryOperation;
import expression.generic.operations.Expression;
import expression.generic.operations.UnaryOperation;
import expression.generic.parser.ExpressionParser;
import expression.generic.type.*;

import java.util.Map;

public class GenericTabulator implements Tabulator {
     static Map<String, Mode<? extends Number>> modes = Map.of(
            "i", new CheckedIntegerMode(),
            "d", new DoubleMode(),
            "bi", new BigIntegerMode(),
             "u", new IntegerMode(),
             "b", new ByteMode(),
             "sat", new SatMode()
    );

    public static void main(String[] args) throws ParsingException {
        if (args.length != 2) {
            System.err.println("Usage: TYPE EXPRESSION");
            System.exit(1);
        }
        String type = args[0].substring(1);
        String expression = args[1];
        GenericTabulator tabulator = new GenericTabulator();
        Number[][][] arr = tabulator.tabulate(type, expression, -2, 2, -2, 2, -2, 2);

        for (var x = 0; x < 5; x++) {
            for (var y = 0; y < 5; y++) {
                for (var z = 0; z < 5; z++) {
                    System.out.printf("arr[%d][%d][%d]: %s\n", x - 2, y - 2, z - 2, arr[x][y][z]);
                }
            }
        }
    }


    public Number[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) 
            throws ParsingException {
        Mode<? extends Number> m = modes.get(mode);
        return tabulateImpl(m, expression, x1, x2, y1, y2, z1, z2);
    }

    private <T extends Number> Number[][][] tabulateImpl(Mode<T> mode, String expression,
                                                     int x1, int x2,
                                                     int y1, int y2,
                                                     int z1, int z2) throws ParsingException {
        Expression<T> expr = getExpression(expression);
        Number[][][] result = new Number[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        result[i - x1][j - y1][k - z1] = expr.evaluate(
                                mode,
                                mode.getFromInt(i),
                                mode.getFromInt(j),
                                mode.getFromInt(k)
                        );
                    } catch (EvaluateException | ArithmeticException ex) {
                        result[i - x1][j - y1][k - z1] = null;
                    }
                }
            }
        }

        return result;
    }

    private static <T extends Number> Expression<T> getExpression(String expression) throws ParsingException {
        ExpressionParser<T> parser = new ExpressionParser<>(
                Map.of(
                        "+", new BinaryOperation<>(Mode::add, Priority.COMMON), // :NOTE: числовые приоритеты
                        "-", new BinaryOperation<>(Mode::subtract, Priority.COMMON),
                        "*", new BinaryOperation<>(Mode::multiply, Priority.HIGH),
                        "/", new BinaryOperation<>(Mode::divide, Priority.HIGH),
                        "min", new BinaryOperation<>(Mode::min, Priority.LOW),
                        "max", new BinaryOperation<>(Mode::max, Priority.LOW)
                ),
                Map.of(
                        "-", new UnaryOperation<>(Mode::negate),
                        "count", new UnaryOperation<>(Mode::count)

                )
        );

        return parser.parse(expression);
    }
}
