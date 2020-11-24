package com.company.bankaccounts.controller.dto;

import net.minidev.json.annotate.JsonIgnore;

import java.math.BigDecimal;

public class AccountDTO {

	@JsonIgnore
	private String id;

	private String name;
	private String surname;
	private String pin;
	private BigDecimal amount;	// in EUR

	public AccountDTO(String id, String name, String surname, String pin, BigDecimal amount) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.pin = pin;
		this.amount = amount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}