package expression;

import exceptions.ParseExceptions;

public class Variable<T> implements TripleExpression<T> {
	private char value;

	public Variable(String x) {
		value = x.charAt(0);
	}

	@Override
	public T evaluate(T x, T y, T z) {
		switch (value) {
		case 'x':
			return x;
		case 'y':
			return y;
		case 'z':
			return z;
		default:
			return x;
		}
	}

}
