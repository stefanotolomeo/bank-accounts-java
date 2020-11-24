package com.company.bankaccounts.dao.model;

import java.math.BigDecimal;

public class TransactionTransfer extends AbstractTransaction {

	private String fromAccountId;	// from account ID
	private String toAccountId;		// to account ID

	public TransactionTransfer(String id, BigDecimal amount, String fromAccountId, String toAccountId) {
		super(TransactionType.TRANSFER, id, amount);
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
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

	@Override
	public String toString() {
		return "TransactionTransfer{" + "fromAccountId='" + fromAccountId + '\'' + ", toAccountId='" + toAccountId + '\'' + ", id='" + id
				+ '\'' + ", amount=" + amount + '}';
	}
}
