package io.github.css12345.sourceanalyse.similarityanalyse.exception;

public class SimilarityCalculateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SimilarityCalculateException() {

	}

	public SimilarityCalculateException(String message) {
		super(message);
	}

	public SimilarityCalculateException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimilarityCalculateException(Throwable cause) {
		super(cause);
	}
}
