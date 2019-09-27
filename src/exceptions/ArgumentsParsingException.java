package exceptions;

//import java.text.ParseException;

public class ArgumentsParsingException extends ParseExceptions {
	public ArgumentsParsingException(int ind) {
		super("wrong arguments", ind);
	}
}
