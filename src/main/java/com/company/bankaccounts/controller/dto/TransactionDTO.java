package com.company.bankaccounts.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class TransactionDTO {
	// This class is needed because it is the returned type of Transaction-RestController

	@JsonProperty(value = "type", access = JsonProperty.Access.READ_ONLY)
	protected TransactionTypeDTO typeDTO;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String pin;	// Only for deserialize input, not used for serialize response

	public TransactionDTO(TransactionTypeDTO typeDTO, String id) {
		this.typeDTO = typeDTO;
		this.id = id;
	}

	@JsonIgnore
	@JsonProperty(value = "type")
	public TransactionTypeDTO getTypeDTO() {
		return typeDTO;
	}

	public void setTypeDTO(TransactionTypeDTO typeDTO) {
		this.typeDTO = typeDTO;
	}

	@JsonIgnore
	@JsonProperty(value = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPin() {
		return pin;
	}

	@JsonIgnore
	@JsonProperty(value = "pin")
	public void setPin(String pin) {
		this.pin = pin;
	}

}
