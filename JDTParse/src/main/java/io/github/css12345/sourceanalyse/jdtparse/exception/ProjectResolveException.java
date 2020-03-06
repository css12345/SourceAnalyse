package io.github.css12345.sourceanalyse.jdtparse.exception;

public class ProjectResolveException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProjectResolveException() {
		super();
	}

	public ProjectResolveException(String message) {
		super(message);
	}

	public ProjectResolveException(Throwable cause) {
		super(cause);
	}

	public ProjectResolveException(String message, Throwable cause) {
		super(message, cause);
	}
}
