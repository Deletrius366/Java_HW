package exceptions;

public class WrongSymbolException extends ParseExceptions {
	public WrongSymbolException (int ind) {
		super("Unknown Symbol", ind);
	}
}

