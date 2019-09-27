package expression;

public interface CommonExpression extends TripleExpression, DoubleExpression, Expression {
	interface Expression{ };
	interface DoubleExpression { };
	interface TripleExpression { };
}
