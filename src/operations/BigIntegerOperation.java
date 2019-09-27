package operations;

import java.math.BigInteger;

import exceptions.DivisionByZeroException;

public class BigIntegerOperation implements Operation<BigInteger> {

	@Override
	public BigInteger add(BigInteger x, BigInteger y) {
		return x.add(y);
	}

	@Override
	public BigInteger sub(BigInteger x, BigInteger y) {
		return x.subtract(y);
	}

	@Override
	public BigInteger mul(BigInteger x, BigInteger y) {
		return x.multiply(y);
	}

	private void checkDiv(BigInteger x, BigInteger y) throws DivisionByZeroException {
		if (y.equals(BigInteger.ZERO)) {
			throw new DivisionByZeroException();
		}
	}

	@Override
	public BigInteger div(BigInteger x, BigInteger y) throws DivisionByZeroException {
		checkDiv(x, y);
		return x.divide(y);
	}

	@Override
	public BigInteger neg(BigInteger x) {
		return x.negate();
	}

	@Override
	public BigInteger parseNum(String x) {
		return new BigInteger(x);
	}

	@Override
	public BigInteger abs(BigInteger x) {
		return x.abs();
	}

	@Override
	public BigInteger square(BigInteger x) {
		return x.multiply(x);
	}

	@Override
	public BigInteger mod(BigInteger x, BigInteger y) throws DivisionByZeroException {
		if (y.compareTo(BigInteger.ZERO) <= 0) {
			throw new DivisionByZeroException();
		}
		return x.mod(y);
	}

}
