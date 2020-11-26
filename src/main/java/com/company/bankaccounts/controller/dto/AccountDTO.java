package com.company.bankaccounts.controller.dto;

import java.math.BigDecimal;

public class AccountDTO extends BaseAccountDTO {

	private BigDecimal amount;    // in EUR

	public AccountDTO(String id, String name, String surname, String pin, BigDecimal amount) {
		super(name, surname, pin);
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "AccountDTO{" + "amount=" + amount + ", name='" + name + '\'' + ", surname='" + surname + '\'' + ", pin='" + pin + '\''
				+ '}';
	}
}
