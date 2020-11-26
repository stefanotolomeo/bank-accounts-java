package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.AccountDTO;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.testconfig.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

class AccountValidatorTest extends BaseTest {

	@Autowired
	private AccountValidator accountValidator;

	@DisplayName("Validate Account: for POST request")
	@Test
	void validateAccount_POST_Test() {

		// (1) Invalid Account: null
		validateAndAssertException(null, "Invalid Account: Null Account");

		// (2) Invalid Account: null name
		AccountDTO invalidAccount = new AccountDTO(null, null, null, null, null);
		validateAndAssertException(invalidAccount, "Invalid Account: Null or Empty Name");

		// (3) Invalid Account: null surname
		invalidAccount.setName("NAME");
		validateAndAssertException(invalidAccount, "Invalid Account: Null or Empty Surname");

		// (4) Invalid Account: null pin
		invalidAccount.setSurname("SURNAME");
		validateAndAssertException(invalidAccount, "Invalid Account: Null or Empty Pin");

		// (5) Invalid Account: empty pin
		invalidAccount.setPin("");
		validateAndAssertException(invalidAccount, "Invalid Account: Null or Empty Pin");

		// (6) Invalid Account: bad pin chars (4 chars but at least one no-digit)
		invalidAccount.setPin("1A34");
		validateAndAssertException(invalidAccount, "Invalid Account: Invalid Pin: only digits allowed");

		// (7) Invalid Account: bad pin format (4 chars but empty spaces)
		invalidAccount.setPin("1   ");
		validateAndAssertException(invalidAccount, "Invalid Account: Invalid Pin: only digits allowed");

		// (8) Invalid Account: bad pin size (lower)
		invalidAccount.setPin("123");
		validateAndAssertException(invalidAccount, "Invalid Account: Invalid Pin: only 4 digits allowed");

		// (9) Invalid Account: bad pin size (greater)
		invalidAccount.setPin("12345");
		validateAndAssertException(invalidAccount, "Invalid Account: Invalid Pin: only 4 digits allowed");

		// (10) Invalid Account: null amount
		invalidAccount.setPin("1234");
		validateAndAssertException(invalidAccount, "Invalid Account: Null Amount");

		// (11) Valid Account: no exception here
		invalidAccount.setAmount(BigDecimal.TEN);
		accountValidator.commonValidation(invalidAccount.getName(), invalidAccount.getSurname(), invalidAccount.getPin());
	}

	@DisplayName("Validate Account: For PUT Request")
	@Test
	void validateAccount_PUT_Test() {
		AccountDTO validAccount = new AccountDTO(null, "Name", "Surname", "1234", BigDecimal.TEN);

		// (1) Invalid ID: Null
		validateAndAssertException(validAccount, null, "Invalid Account: Null or Empty ID");

		// (2) Invalid ID: Empty
		validateAndAssertException(validAccount, "", "Invalid Account: Null or Empty ID");

		// (3) Invalid ID: Empty spaces
		validateAndAssertException(validAccount, "  ", "Invalid Account: Null or Empty ID");

		// Other assertions are not needed

	}

	private void validateAndAssertException(AccountDTO acc, String msg) {
		InvalidInputException e = Assertions.assertThrows(InvalidInputException.class, () -> accountValidator.validate(acc));
		Assertions.assertEquals(msg, e.getMessage());
	}

	private void validateAndAssertException(AccountDTO acc, String accId, String msg) {
		InvalidInputException e = Assertions.assertThrows(InvalidInputException.class, () -> accountValidator.validate(acc, accId));
		Assertions.assertEquals(msg, e.getMessage());
	}

}
