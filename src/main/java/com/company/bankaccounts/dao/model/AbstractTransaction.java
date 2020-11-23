package com.company.bankaccounts.dao.model;

import java.math.BigDecimal;

public abstract class AbstractTransaction {
	private final TransactionType transactionType;

	private String id;

	private BigDecimal amount; // Amount in EUR

	AbstractTransaction(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
