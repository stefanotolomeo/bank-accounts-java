package com.company.bankaccounts.controller.dto;

import java.math.BigDecimal;

public class WithdrawTransactionDTO {

	private BigDecimal amount;
	private String accountId;
	private String pin;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}
