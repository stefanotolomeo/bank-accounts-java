package com.company.bankaccounts.dao.model;

import java.math.BigDecimal;

public class TransactionWithdraw extends AbstractTransaction {

	private String accountId;	// from account ID

	public TransactionWithdraw(String id, BigDecimal amount, String accountId) {
		super(TransactionType.WITHDRAW, id, amount);
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
		return "TransactionWithdraw{" + "accountId='" + accountId + '\'' + ", id='" + id + '\'' + ", amount=" + amount + '}';
	}
}
