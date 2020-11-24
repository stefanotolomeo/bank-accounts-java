package com.company.bankaccounts.dao.model;

import java.math.BigDecimal;

public class TransactionDeposit extends AbstractTransaction {

	private String accountId;	// beneficiary account ID

	public TransactionDeposit(String id, BigDecimal amount, String accountId) {
		super(TransactionType.DEPOSIT, id, amount);
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		return "TransactionDeposit{" + "accountId='" + accountId + '\'' + ", id='" + id + '\'' + ", amount=" + amount + '}';
	}
}
