package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.testconfig.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TransactionValidatorTest extends BaseTest {

	@Autowired
	private TransactionValidator transactionValidator;

	@DisplayName("Check Pin")
	@Test
	void checkPin_Test() {
		// TODO: mock accountManager and return a specific account
		// transactionValidator.checkPin();

	}

	// TODO: other tests
}
