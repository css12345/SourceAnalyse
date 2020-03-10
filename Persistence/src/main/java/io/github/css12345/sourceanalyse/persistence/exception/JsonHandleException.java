package io.github.css12345.sourceanalyse.persistence.exception;

public class JsonHandleException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JsonHandleException() {

	}

	public JsonHandleException(String message) {
		super(message);
	}

	public JsonHandleException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonHandleException(Throwable cause) {
		super(cause);
	}
}
