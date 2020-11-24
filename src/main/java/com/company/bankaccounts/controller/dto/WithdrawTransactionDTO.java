package com.company.bankaccounts.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class WithdrawTransactionDTO extends TransactionDTO {

	private BigDecimal amount;
	private String accountId;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String pin;	// Only for deserialize input, not used for serialize response

	public WithdrawTransactionDTO(String id, BigDecimal amount, String accountId) {
		super(TransactionTypeDTO.WITHDRAW, id);
		this.amount = amount;
		this.accountId = accountId;
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
		return "WithdrawTransactionDTO{" + "amount=" + amount + ", accountId='" + accountId + '\'' + ", pin='" + pin + '\'' + '}';
	}
}
