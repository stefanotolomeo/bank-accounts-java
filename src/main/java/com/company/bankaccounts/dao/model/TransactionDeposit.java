package com.company.bankaccounts.dao.model;

import java.math.BigDecimal;

public class TransactionDeposit extends AbstractTransaction {

	private static final long serialVersionUID = 2035470831015184493L;

	// Used by REDIS default serializer
	public TransactionDeposit() {
	}

	// Account is the beneficiary account ID
	public TransactionDeposit(String id, BigDecimal amount, String accountId) {
		super(TransactionType.DEPOSIT, id, amount, accountId);
	}

	@Override
	public String toString() {
		return "TransactionDeposit{" + "accountId='" + accountId + '\'' + ", id='" + id + '\'' + ", amount=" + amount + '}';
	}
}
