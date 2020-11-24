package com.company.bankaccounts.controller.dto;

import java.math.BigDecimal;

public class TransferTransactionDTO extends TransactionDTO {

	private BigDecimal amount;
	private String fromAccountId;
	private String toAccountId;

	public TransferTransactionDTO(String id, BigDecimal amount, String fromAccountId, String toAccountId) {
		super(TransactionTypeDTO.TRANSFER, id);
		this.amount = amount;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
		return "TransferTransactionDTO{" + "amount=" + amount + ", fromAccountId='" + fromAccountId + '\'' + ", toAccountId='" + toAccountId
				+ '\'' + ", pin='" + pin + '\'' + ", typeDTO=" + typeDTO + ", id='" + id + '\'' + '}';
	}
}
