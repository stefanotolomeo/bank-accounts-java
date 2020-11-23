package com.company.bankaccounts.dao.model;

public class TransactionTransfer {

	private final TransactionType transactionType = TransactionType.TRANSFER;
	private String id;
	private String fromAccountId;	// from account ID
	private String toAccountId;		// to account ID

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromAccountId() {
		return fromAccountId;
	}

	public void setFromAccountId(String fromAccountId) {
		this.fromAccountId = fromAccountId;
	}

	public String getToAccountId() {
		return toAccountId;
	}

	public void setToAccountId(String toAccountId) {
		this.toAccountId = toAccountId;
	}
}
