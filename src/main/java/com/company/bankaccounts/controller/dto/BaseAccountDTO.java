package com.company.bankaccounts.controller.dto;

public class BaseAccountDTO {

	protected String name;
	protected String surname;
	protected String pin;

	public BaseAccountDTO(String name, String surname, String pin) {
		this.name = name;
		this.surname = surname;
		this.pin = pin;
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

}
