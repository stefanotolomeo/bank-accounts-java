package com.company.bankaccounts.dao.model;

import java.io.Serializable;
import java.math.BigDecimal;

public abstract class AbstractTransaction implements Serializable {
	private final TransactionType transactionType;

	protected String id;
	protected BigDecimal amount; // Amount in EUR

	public AbstractTransaction(TransactionType transactionType, String id, BigDecimal amount) {
		this.transactionType = transactionType;
		this.id = id;
		this.amount = amount;
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
