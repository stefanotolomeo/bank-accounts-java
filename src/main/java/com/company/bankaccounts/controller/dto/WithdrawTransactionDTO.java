package com.company.bankaccounts.controller.dto;

import java.math.BigDecimal;

public class WithdrawTransactionDTO extends TransactionDTO {

	private BigDecimal amount;
	private String accountId;

	public WithdrawTransactionDTO(String id, BigDecimal amount, String accountId) {
		super(TransactionTypeDTO.WITHDRAW, id);
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
		return "WithdrawTransactionDTO{" + "amount=" + amount + ", accountId='" + accountId + '\'' + ", pin='" + pin + '\'' + ", typeDTO="
				+ typeDTO + ", id='" + id + '\'' + '}';
	}
}
