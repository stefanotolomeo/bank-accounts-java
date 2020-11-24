package com.company.bankaccounts.exceptions;

public class InvalidInputException extends Exception {

	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}
}
