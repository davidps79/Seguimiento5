package exception;

public class UnexpectedInputException extends Exception{
	private static final long serialVersionUID = 1L;

	public UnexpectedInputException(String m) {
		super(m);
	}
}
