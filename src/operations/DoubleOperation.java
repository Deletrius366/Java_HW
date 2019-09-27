package operations;

public class DoubleOperation implements Operation<Double> {

	@Override
	public Double add(Double x, Double y) {
		return x + y;
	}

	@Override
	public Double sub(Double x, Double y) {
		return x - y;
	}

	@Override
	public Double mul(Double x, Double y) {
		return x * y;
	}

	@Override
	public Double div(Double x, Double y) {
		return x / y;
	}

	@Override
	public Double neg(Double x) {
		return -x;
	}

	@Override
	public Double parseNum(String x) {
		return Double.parseDouble(x);
	}

	@Override
	public Double abs(Double x) {
		return Math.abs(x);
	}

	@Override
	public Double square(Double x) {
		return x*x;
	}

	@Override
	public Double mod(Double x, Double y) {
		return x % y;
	}

}
