package operations;

import exceptions.DivisionByZeroException;
import exceptions.OverflowException;

public class FloatOperation implements Operation<Float> {

	@Override
	public Float add(Float x, Float y) throws OverflowException {
		return x + y;
	}

	@Override
	public Float sub(Float x, Float y) throws OverflowException {
		return x - y;
	}

	@Override
	public Float mul(Float x, Float y) throws OverflowException {
		return x * y;
	}

	@Override
	public Float div(Float x, Float y) throws DivisionByZeroException, OverflowException {
		return x / y;
	}

	@Override
	public Float neg(Float x) {
		return -x;
	}

	@Override
	public Float abs(Float x) {
		return Math.abs(x);
	}

	@Override
	public Float square(Float x) {
		return x * x;
	}

	@Override
	public Float mod(Float x, Float y) {
		return x % y;
	}

	@Override
	public Float parseNum(String x) {
		return Float.parseFloat(x);
	}

}
