package operations;

import exceptions.*;

public class ByteOperation implements Operation<Byte> {

	@Override
	public Byte add(Byte x, Byte y) throws OverflowException {
		return (byte) (x + y);
	}

	@Override
	public Byte sub(Byte x, Byte y) throws OverflowException {
		return (byte) (x - y);
	}

	@Override
	public Byte mul(Byte x, Byte y) throws OverflowException {
		return (byte) (x * y);
	}

	@Override
	public Byte div(Byte x, Byte y) throws DivisionByZeroException, OverflowException {
		if (y == 0) {
			throw new DivisionByZeroException();
		}
		return (byte) (x / y);
	}

	@Override
	public Byte neg(Byte x) {
		return (byte) (-x);
	}

	@Override
	public Byte abs(Byte x) {
		return (byte) Math.abs(x);
	}

	@Override
	public Byte square(Byte x) {
		return (byte) (x * x);
	}

	@Override
	public Byte mod(Byte x, Byte y) throws DivisionByZeroException {
		if (y == 0) {
			throw new DivisionByZeroException();
		}
		return (byte) (x % y);
	}

	@Override
	public Byte parseNum(String x) throws ParseConstException {
		try {
			return (byte) Integer.parseInt(x);
		} catch (Exception e) {
			throw new ParseConstException(0);
		}
	}

}
