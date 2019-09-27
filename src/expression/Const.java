package expression;

import exceptions.ParseExceptions;

public class Const<T> implements TripleExpression<T> {
	private T value;

	public Const(T x) {
		value = x;
	}

	@Override
	public T evaluate(T x, T y, T z) {
		return value;
	}

}
