package com.company.bankaccounts.dao.model;

public class TransactionWithdraw {

	private final TransactionType transactionType = TransactionType.WITHDRAW;
	private String id;
	private String accountId;	// from account ID

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
