package exceptions;

public class ParseExceptions extends Exception {
	public ParseExceptions (String s, int pos) {
		super(s + "at position: " + pos);
	}
}
