package com.company.bankaccounts.dao.model;

public class TransactionDeposit {

	private final TransactionType transactionType = TransactionType.DEPOSIT;
	private String id;
	private String accountId;	// beneficiary account ID

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
