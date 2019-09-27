package expression;

import exceptions.OverflowException;
import exceptions.ParseExceptions;
import operations.Operation;

public class CheckedSubtract<T> extends AbstractBinaryOper<T> {

	public CheckedSubtract(TripleExpression<T> x, TripleExpression<T> y, Operation<T> op) {
		super(x, y, op);
	}

	@Override
	protected T apply(T x, T y) throws OverflowException {
		return oper.sub(x, y);
	}

}
