package com.nsi.exception;

public class ValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getMessage() {
		return "[VALIDATION] " + super.getMessage();
	}
	
	public String getActualMessage() {
		return super.getMessage();
	}
}
