package com.company.bankaccounts.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TransferTransactionDTO extends TransactionDTO {

	private BigDecimal amount;
	private String fromAccountId;
	private String toAccountId;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String pin;	// Only for deserialize input, not used for serialize response

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

	public String getPin() {
		return pin;
	}

	@JsonIgnore
	@JsonProperty(value = "pin")
	public void setPin(String pin) {
		this.pin = pin;
	}

	@Override
	public String toString() {
		return "TransferTransactionDTO{" + "amount=" + amount + ", fromAccountId='" + fromAccountId + '\'' + ", toAccountId='" + toAccountId
				+ '\'' + ", pin='" + pin + '\'' + '}';
	}
}
