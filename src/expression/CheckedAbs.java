package expression;

import exceptions.OverflowException;
import exceptions.ParseExceptions;
import operations.Operation;

public class CheckedAbs<T> extends AbstractUnaryOper<T>{

	public CheckedAbs(TripleExpression<T> x, Operation<T> op) {
		super(x,op);
	}


	@Override
	protected T apply(T x) throws OverflowException {
		return oper.abs(x);
	}

}
