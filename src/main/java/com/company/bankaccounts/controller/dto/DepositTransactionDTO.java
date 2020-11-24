package com.company.bankaccounts.controller.dto;

import java.math.BigDecimal;

public class DepositTransactionDTO extends TransactionDTO {

	private BigDecimal amount;
	private String accountId;

	public DepositTransactionDTO(String id, BigDecimal amount, String accountId) {
		super(TransactionTypeDTO.DEPOSIT, id);
		this.amount = amount;
		this.accountId = accountId;
	}

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

	@Override
	public String toString() {
		return "DepositTransactionDTO{" + "amount=" + amount + ", accountId='" + accountId + '\'' + ", pin='" + pin + '\'' + ", typeDTO="
				+ typeDTO + ", id='" + id + '\'' + '}';
	}
}
