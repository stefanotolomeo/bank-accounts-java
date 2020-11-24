package com.company.bankaccounts.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class TransactionDTO {
	// This class is needed because it is the returned type of Transaction-RestController

	@JsonProperty("type")
	protected TransactionTypeDTO typeDTO;
	protected String id;

	public TransactionDTO(TransactionTypeDTO typeDTO, String id) {
		this.typeDTO = typeDTO;
		this.id = id;
	}

	public TransactionTypeDTO getTypeDTO() {
		return typeDTO;
	}

	public void setTypeDTO(TransactionTypeDTO typeDTO) {
		this.typeDTO = typeDTO;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
