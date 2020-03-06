package io.github.css12345.sourceanalyse.jdtparse.exception;

public class DependencyResolveException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DependencyResolveException() {
		super();
	}

	public DependencyResolveException(String message) {
		super(message);
	}

	public DependencyResolveException(Throwable cause) {
		super(cause);
	}

	public DependencyResolveException(String message, Throwable cause) {
		super(message, cause);
	}
}
