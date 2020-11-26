package com.company.bankaccounts.dao.model;

import java.math.BigDecimal;

public class TransactionWithdraw extends AbstractTransaction {

	private static final long serialVersionUID = 7239752053177850056L;

	// Used by REDIS default serializer
	public TransactionWithdraw() {
	}

	// Account is the source account ID
	public TransactionWithdraw(String id, BigDecimal amount, String accountId) {
		super(TransactionType.WITHDRAW, id, amount, accountId);
	}

	@Override
	public String toString() {
		return "TransactionWithdraw{" + "accountId='" + accountId + '\'' + ", id='" + id + '\'' + ", amount=" + amount + '}';
	}
}
