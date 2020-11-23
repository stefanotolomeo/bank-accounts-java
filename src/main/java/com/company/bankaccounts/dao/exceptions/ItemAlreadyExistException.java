package com.company.bankaccounts.dao.exceptions;

public class ItemAlreadyExistException extends Exception {

	public ItemAlreadyExistException(String message) {
		super(message);
	}

	public ItemAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}
}