import expression.*;
import expression.generic.GenericTabulator;
import expression.parser.*;
import exceptions.*;

public class rofl {
	public static void main(String[] args) throws Exception {

		int a = 0;
		a++;
		int b = a;
		a += b;

		Object[][][] res = new GenericTabulator().tabulate("i", "(-x))", Integer.MIN_VALUE, Integer.MIN_VALUE, 2147483644, 2147483646, 100, 100);
		System.out.println(res[0][0][0]);
	}
}