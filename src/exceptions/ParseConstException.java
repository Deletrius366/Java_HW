package exceptions;

//import java.text.ParseException;

public class ParseConstException extends ParseExceptions {
	public ParseConstException(int ind) {
		super("cant parse constant", ind);
	}
}
