package com.company.bankaccounts.dao.model;

public class TransactionTransfer extends AbstractTransaction {

	private String fromAccountId;	// from account ID
	private String toAccountId;		// to account ID

	public TransactionTransfer() {
		super(TransactionType.TRANSFER);
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
