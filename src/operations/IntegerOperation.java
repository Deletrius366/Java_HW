package operations;

import exceptions.DivisionByZeroException;
import exceptions.OverflowException;
import exceptions.ParseConstException;
import exceptions.ParseToIntException;

public class IntegerOperation implements Operation<Integer> {
	private boolean checked;

	public IntegerOperation(boolean checked) {
		this.checked = checked;
	}

	private void checkAdd(Integer x, Integer y) throws OverflowException {
		if (x > 0 && Integer.MAX_VALUE - x < y) {
			throw new OverflowException();
		}
		if (x < 0 && Integer.MIN_VALUE - x > y) {
			throw new OverflowException();
		}
	}

	@Override
	public Integer add(Integer x, Integer y) throws OverflowException {
		if (checked) {
			checkAdd(x, y);
		}
		return x + y;
	}

	private void checkSub(Integer x, Integer y) throws OverflowException {
		if (y == Integer.MIN_VALUE && x >= 0) {
			throw new OverflowException();
		}
		if (x <= 0 && y > 0 && Integer.MIN_VALUE - x > -y) {
			throw new OverflowException();
		}
		if (x >= 0 && y < 0 && Integer.MAX_VALUE - x < -y) {
			throw new OverflowException();
		}
	}

	@Override
	public Integer sub(Integer x, Integer y) throws OverflowException {
		if (checked) {
			checkSub(x, y);
		}
		return x - y;
	}

	private void checkMul(Integer x, Integer y) throws OverflowException {
		if (x > 0 && y > 0 && Integer.MAX_VALUE / x < y) {
			throw new OverflowException();
		}

		if (x > 0 && y < 0 && Integer.MIN_VALUE / x > y) {
			throw new OverflowException();
		}

		if (x < 0 && y > 0 && Integer.MIN_VALUE / y > x) {
			throw new OverflowException();
		}

		if (x < 0 && y < 0 && Integer.MAX_VALUE / x > y) {
			throw new OverflowException();
		}
	}

	@Override
	public Integer mul(Integer x, Integer y) throws OverflowException {
		if (checked) {
			checkMul(x, y);
		}
		return x * y;
	}

	private void checkDiv(Integer x, Integer y) throws DivisionByZeroException, OverflowException {
		if (y == 0) {
			throw new DivisionByZeroException();
		}
		if (x == Integer.MIN_VALUE && y == -1) {
			throw new OverflowException();
		}
	}

	@Override
	public Integer div(Integer x, Integer y) throws DivisionByZeroException, OverflowException {
		if (checked) {
			checkDiv(x, y);
		}
		if (y == 0) {
			throw new DivisionByZeroException();
		}
		return x / y;
	}

	private void checkNeg(Integer x) throws OverflowException {
		if (x == Integer.MIN_VALUE) {
			throw new OverflowException();
		}
	}

	@Override
	public Integer neg(Integer x) throws OverflowException {
		if (checked) {
			checkNeg(x);
		}
		return -x;
	}

	@Override
	public Integer parseNum(String x) throws ParseConstException {
		try {
			return Integer.parseInt(x);
		} catch (Exception e) {
			throw new ParseConstException(0);
		}
	}

	private void checkAbs(Integer x) throws OverflowException {
		if (x == Integer.MIN_VALUE) {
			throw new OverflowException();
		}
	}

	@Override
	public Integer abs(Integer x) throws OverflowException {
		if (checked) {
			checkAbs(x);
		}
		return Math.abs(x);
	}

	@Override
	public Integer square(Integer x) throws OverflowException {
		if (checked) {
			checkMul(x, x);
		}
		return x * x;
	}

	@Override
	public Integer mod(Integer x, Integer y) throws DivisionByZeroException {
		if (y == 0) {
			throw new DivisionByZeroException();
		}
		return x % y;
	}

}
