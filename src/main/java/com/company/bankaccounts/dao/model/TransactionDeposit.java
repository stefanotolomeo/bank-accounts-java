package com.company.bankaccounts.dao.model;

public class TransactionDeposit extends AbstractTransaction {

	private String accountId;	// beneficiary account ID

	public TransactionDeposit() {
		super(TransactionType.DEPOSIT);
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
