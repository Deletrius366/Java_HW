package expression;

import exceptions.EvaluateExceptions;
import exceptions.ParseExceptions;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface TripleExpression<T> {
	T evaluate(T x, T y, T z) throws EvaluateExceptions;
}
