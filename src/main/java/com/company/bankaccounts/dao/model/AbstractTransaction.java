package com.company.bankaccounts.dao.model;

import java.io.Serializable;
import java.math.BigDecimal;

public abstract class AbstractTransaction implements Serializable {

	private static final long serialVersionUID = 4601465263029245494L;

	private TransactionType type;

	protected String id;
	protected BigDecimal amount; // Amount in EUR
	protected String accountId;    // source (for Withdrawal and Transfer) or destination (for Deposit)

	// Used by REDIS default serializer
	public AbstractTransaction() {
	}

	public AbstractTransaction(TransactionType type, String id, BigDecimal amount, String accountId) {
		this.type = type;
		this.id = id;
		this.amount = amount;
		this.accountId = accountId;
	}

	public TransactionType getType() {
		return type;
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

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
