package io.github.css12345.sourceanalyse.jdtparse.exception;

public class CommandExecuteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CommandExecuteException() {
		super();
	}

	public CommandExecuteException(String message) {
		super(message);
	}
	
	public CommandExecuteException(Throwable cause) {
		super(cause);
	}

	public CommandExecuteException(String message, Throwable  cause) {
		super(message, cause);
	}
}
