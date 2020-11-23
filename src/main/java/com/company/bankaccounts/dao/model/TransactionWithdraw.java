package com.company.bankaccounts.dao.model;

public class TransactionWithdraw extends AbstractTransaction {

	private String accountId;	// from account ID

	public TransactionWithdraw() {
		super(TransactionType.WITHDRAW);
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
