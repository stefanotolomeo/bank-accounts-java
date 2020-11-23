package com.company.bankaccounts.dao.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OperationType {
	//@formatter:off
	@JsonProperty("INSERT")
	INSERT,
	@JsonProperty("UPDATE")
	UPDATE,
	@JsonProperty("DELETE")
	DELETE
	//@formatter:on
}