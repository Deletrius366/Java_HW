package expression;

import exceptions.EvaluateExceptions;
import exceptions.ParseExceptions;
import operations.Operation;

//import exceptions.OverflowException;

public abstract class AbstractBinaryOper<T> implements TripleExpression<T> {
	private TripleExpression<T> first;
	private TripleExpression<T> second;
	protected Operation<T> oper;

	protected AbstractBinaryOper(TripleExpression<T> x, TripleExpression<T> y, Operation<T> op) {
		first = x;
		second = y;
		oper = op;
	}

	protected abstract T apply(T x, T y) throws EvaluateExceptions;

/*	protected abstract void check(int x, int y) throws ParseExceptions;

	public int evaluate(int x) throws ParseExceptions {
		return apply(first.evaluate(x), second.evaluate(x));
	}

	protected abstract double apply(double x, double y);

	public double evaluate(double x) {
		return apply(first.evaluate(x), second.evaluate(x));
	}
	*/
	public T evaluate(T x, T y, T z) throws EvaluateExceptions {
		return apply(first.evaluate(x, y, z), second.evaluate(x, y, z));	
	}

}
