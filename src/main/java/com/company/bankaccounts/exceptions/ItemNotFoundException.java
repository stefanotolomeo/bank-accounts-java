package com.company.bankaccounts.exceptions;

public class ItemNotFoundException extends Exception {

	public ItemNotFoundException(String message) {
		super(message);
	}

	public ItemNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}