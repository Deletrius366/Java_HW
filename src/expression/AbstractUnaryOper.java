package expression;

import exceptions.EvaluateExceptions;
import exceptions.ParseExceptions;
import operations.Operation;

public abstract class AbstractUnaryOper<T> implements TripleExpression<T> {
	private TripleExpression<T> operand;
	protected Operation<T> oper;

	protected AbstractUnaryOper(TripleExpression<T> x, Operation<T> op) {
		operand = x;
		oper = op;
	}

	protected abstract T apply(T x) throws EvaluateExceptions;

	/*
	 * protected abstract void check(int x) throws ParseExceptions;
	 * 
	 * public int evaluate(int x) throws ParseExceptions { return
	 * apply(oper.evaluate(x)); }
	 * 
	 * protected abstract double apply(double x);
	 * 
	 * public double evaluate(double x) { return apply(oper.evaluate(x)); }
	 */
	public T evaluate(T x, T y, T z) throws EvaluateExceptions {
		return apply(operand.evaluate(x, y, z));
	}
}
