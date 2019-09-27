package expression;

import exceptions.OverflowException;
import exceptions.ParseExceptions;
import operations.Operation;

public class CheckedMultiply<T> extends AbstractBinaryOper<T> {

	public CheckedMultiply(TripleExpression<T> x, TripleExpression<T> y, Operation<T> op) {
		super(x, y, op);
	}

	@Override
	protected T apply(T x, T y) throws OverflowException {
		return oper.mul(x, y);
	}

}
