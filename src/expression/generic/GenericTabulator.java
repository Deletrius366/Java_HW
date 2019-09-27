package expression.generic;

import exceptions.EvaluateExceptions;
import exceptions.ParseExceptions;
import exceptions.WrongTypeException;
import expression.TripleExpression;
import expression.parser.ExpressionParser;
import expression.parser.Parser;
import operations.*;

import java.util.Map;

public class GenericTabulator implements Tabulator {

	@Override
	public Object[][][] tabulate(String mode, String expr, int x1, int x2, int y1, int y2, int z1, int z2) throws WrongTypeException {
		return makeTable(getOperation(mode), expr, x1, x2, y1, y2, z1, z2);
	}

	private <T> Object[][][] makeTable(Operation<T> oper, String expr, int x1, int x2, int y1, int y2, int z1, int z2) {
		Object[][][] res = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
		TripleExpression<T> exp;
		try {
			exp = new ExpressionParser<>(oper).parse(expr);
		} catch (ParseExceptions e) {
			return res;
		}
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				for (int k = z1; k <= z2; k++) {
					try {
							res[i - x1][j - y1][k - z1] = exp.evaluate(oper.parseNum(Integer.toString(i)),
								oper.parseNum(Integer.toString(j)), oper.parseNum(Integer.toString(k)));
					} catch (ParseExceptions | EvaluateExceptions  e) {
					}
				}
			}
		}
		return res;
	}
	
	private static Map<String, Operation<?>> TYPES = Map.of(
			"i", new IntegerOperation(true),
			"u", new IntegerOperation(false),
			"bi", new BigIntegerOperation(),
			"b", new ByteOperation(),
			"d", new DoubleOperation(),
			"f", new FloatOperation()
	);

	private Operation<?> getOperation(String mode) throws WrongTypeException {
		Operation<?> type = TYPES.get(mode);
		if (type == null) {
			throw new WrongTypeException();
		}
		return type;
	}
}
