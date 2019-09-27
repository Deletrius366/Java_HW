package exceptions;

public class BracketException extends ParseExceptions {
	public BracketException(int ind) {
		super("Wrong number of brackets", ind);
	}
}
