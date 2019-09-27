package expression;

import exceptions.ParseExceptions;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Expression {
    int evaluate(int x) throws ParseExceptions;
}
