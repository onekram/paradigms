package expression.exceptions;

import expression.*;

import java.util.Map;

public class ExpressionParser extends expression.parser.ExpressionParser {

    public ExpressionParser() {
        super(
                Map.of(
                        "+", new BiOp(CheckedAdd::new, 10),
                        "-", new BiOp(CheckedSubtract::new, 10),
                        "*", new BiOp(CheckedMultiply::new, 20),
                        "/", new BiOp(CheckedDivide::new, 20),
                        "min", new BiOp(Min::new, 5),
                        "max", new BiOp(Max::new, 5),
                        "<<", new BiOp(LShift::new, 1),
                        ">>", new BiOp(RShift::new, 1),
                        ">>>", new BiOp(AShift::new, 1)
                ),
                Map.of(
                        "-", new UnOp(CheckedNegate::new)
                )
        );
    }

    protected int parseInt(String value) throws ParsingException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new ConstOverflowException(String.format(value), tokenParser.getPos(), ex);
        }
    }
}
