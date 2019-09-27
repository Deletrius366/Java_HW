package expression;

import exceptions.DivisionByZeroException;
import exceptions.OverflowException;
import exceptions.ParseExceptions;
import operations.Operation;

public class CheckedMod<T> extends AbstractBinaryOper<T> {

	public CheckedMod(TripleExpression<T> x, TripleExpression<T> y, Operation<T> op) {
		super(x, y, op);
	}

	@Override
	protected T apply(T x, T y) throws DivisionByZeroException {
		return oper.mod(x, y);
	}

}
