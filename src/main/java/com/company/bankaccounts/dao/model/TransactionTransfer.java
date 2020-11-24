package com.company.bankaccounts.dao.model;

import java.math.BigDecimal;

public class TransactionTransfer extends AbstractTransaction {

	private static final long serialVersionUID = -4598348759006345491L;
	private String toAccountId;		// to account ID

	public TransactionTransfer(String id, BigDecimal amount, String fromAccountId, String toAccountId) {
		super(TransactionType.TRANSFER, id, amount, fromAccountId);
		this.toAccountId = toAccountId;
	}

	public String getToAccountId() {
		return toAccountId;
	}

	public void setToAccountId(String toAccountId) {
		this.toAccountId = toAccountId;
	}

	@Override
	public String toString() {
		return "TransactionTransfer{" + "fromAccountId='" + accountId + '\'' + ", toAccountId='" + toAccountId + '\'' + ", id='" + id
				+ '\'' + ", amount=" + amount + '}';
	}
}
