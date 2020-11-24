package com.company.bankaccounts.exceptions;

import java.math.BigDecimal;

public class InsufficientAmountException extends Exception {

	private BigDecimal availableFunds;
	private BigDecimal requestedFunds;

	public InsufficientAmountException(String message, BigDecimal availableFunds, BigDecimal requestedFunds) {

		super(message);
		this.availableFunds = availableFunds;
		this.requestedFunds = requestedFunds;
	}

	public InsufficientAmountException(String message, Throwable cause) {
		super(message, cause);
	}

	public BigDecimal getAvailableFunds() {
		return availableFunds;
	}

	public void setAvailableFunds(BigDecimal availableFunds) {
		this.availableFunds = availableFunds;
	}

	public BigDecimal getRequestedFunds() {
		return requestedFunds;
	}

	public void setRequestedFunds(BigDecimal requestedFunds) {
		this.requestedFunds = requestedFunds;
	}
}
