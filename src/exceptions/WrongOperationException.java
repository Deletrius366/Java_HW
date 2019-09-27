package exceptions;

public class WrongOperationException extends ParseExceptions {

	public WrongOperationException(int ind) {
		super("unknown operation", ind);
	}
	
}
