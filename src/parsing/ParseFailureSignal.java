package parsing;


public class ParseFailureSignal extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ParseFailureSignal() {
		super();
	}
	public ParseFailureSignal(String message) {
		super(message);
	}
}
