package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.DepositTransactionDTO;
import com.company.bankaccounts.controller.dto.TransactionDTO;
import com.company.bankaccounts.controller.dto.TransferTransactionDTO;
import com.company.bankaccounts.controller.dto.WithdrawTransactionDTO;
import com.company.bankaccounts.dao.manager.AccountManager;
import com.company.bankaccounts.dao.model.*;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.testconfig.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

class TransactionValidatorTest extends BaseTest {

	@Autowired
	private TransactionValidator transactionValidator;

	@Autowired
	private AccountManager accountManager;

	private final String accountID = "111";
	private final String name = "Name";
	private final String surname = "Surname";
	private final String pin = "1234";
	private final BigDecimal amount = BigDecimal.valueOf(500);

	private final Account account = new Account(accountID, name, surname, pin, amount);

	@DisplayName("Check Pin")
	@Test
	void checkPin_Test() throws Exception {

		// (0) Mock the response from AccountManager into checkPin()
		Mockito.when(accountManager.findById(accountID)).thenReturn(account);

		// (1) Bad Pin: exception expected
		InvalidInputException e = Assertions.assertThrows(InvalidInputException.class, () -> transactionValidator.checkPin(accountID, "5555"));
		Assertions.assertEquals("PIN not valid for AccountID="+accountID, e.getMessage());

		// (2) Correct Pin: no exception expected
		transactionValidator.checkPin(accountID, pin);
	}


	@DisplayName("Validate WithrawTransaction-DTO")
	@Test
	void validateWithdrawDTO_Test() throws Exception {

		// (1) Invalid Transaction: null Amount
		WithdrawTransactionDTO invalidTransaction = new WithdrawTransactionDTO(null, null, null);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null Amount");

		// (2) Invalid Transaction: negative Amount
		invalidTransaction.setAmount(BigDecimal.valueOf(-12));
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (3) Invalid Transaction: zero Amount
		invalidTransaction.setAmount(BigDecimal.ZERO);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (4) Invalid Transaction: null AccountID
		invalidTransaction.setAmount(BigDecimal.TEN);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (5) Invalid Transaction: empty AccountID
		invalidTransaction.setAccountId("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (6) Invalid Transaction: null Pin
		invalidTransaction.setAccountId("123");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty Pin");

		// (7) Invalid Transaction: empty Pin
		invalidTransaction.setPin("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty Pin");

		// (8) Valid Transaction: no exception here
		invalidTransaction.setPin("12345");	// here no check on Pin size
		transactionValidator.validate(invalidTransaction);

	}

	@DisplayName("Validate DepositTransaction-DTO")
	@Test
	void validateDepositDTO_Test() throws Exception {

		// (1) Invalid Transaction: null Amount
		DepositTransactionDTO invalidTransaction = new DepositTransactionDTO(null, null, null);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null Amount");

		// (2) Invalid Transaction: negative Amount
		invalidTransaction.setAmount(BigDecimal.valueOf(-12));
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (3) Invalid Transaction: zero Amount
		invalidTransaction.setAmount(BigDecimal.ZERO);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (4) Invalid Transaction: null AccountID
		invalidTransaction.setAmount(BigDecimal.TEN);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (5) Invalid Transaction: empty AccountID
		invalidTransaction.setAccountId("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (6) Invalid Transaction: null Pin
		invalidTransaction.setAccountId("123");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty Pin");

		// (7) Invalid Transaction: empty Pin
		invalidTransaction.setPin("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty Pin");

		// (6) Valid Transaction: no exception here
		invalidTransaction.setPin("12345"); // here no check on Pin size
		transactionValidator.validate(invalidTransaction);

	}
	@DisplayName("Validate TransferTransaction-DTO")
	@Test
	void validateTransferDTO_Test() throws Exception {

		// (1) Invalid Transaction: null Amount
		TransferTransactionDTO invalidTransaction = new TransferTransactionDTO(null, null, null, null);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null Amount");

		// (2) Invalid Transaction: negative Amount
		invalidTransaction.setAmount(BigDecimal.valueOf(-12));
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (3) Invalid Transaction: zero Amount
		invalidTransaction.setAmount(BigDecimal.ZERO);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (4) Invalid Transaction: null From-AccountID
		invalidTransaction.setAmount(BigDecimal.TEN);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (5) Invalid Transaction: empty From-AccountID
		invalidTransaction.setFromAccountId("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (6) Invalid Transaction: null Pin
		invalidTransaction.setFromAccountId("123");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty Pin");

		// (7) Invalid Transaction: empty Pin
		invalidTransaction.setPin("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty Pin");

		// (8) Invalid Transaction: null To-AccountID
		invalidTransaction.setPin("12345"); // here no check on Pin size
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty To-AccountID");

		// (9) Invalid Transaction: empty To-AccountID
		invalidTransaction.setToAccountId("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty To-AccountID");

		// (10) Valid Transaction: no exception here
		invalidTransaction.setToAccountId("456");
		transactionValidator.validate(invalidTransaction);
	}



	private void validateAndAssertException(TransactionDTO trans, String msg) throws Exception {
		InvalidInputException e;
		switch (trans.getTypeDTO()){
			case WITHDRAW:
				e = Assertions.assertThrows(InvalidInputException.class, () -> transactionValidator.validate((WithdrawTransactionDTO) trans));
				break;
			case DEPOSIT:
				e = Assertions.assertThrows(InvalidInputException.class, () -> transactionValidator.validate((DepositTransactionDTO) trans));
				break;
			case TRANSFER:
				e = Assertions.assertThrows(InvalidInputException.class, () -> transactionValidator.validate((TransferTransactionDTO) trans));
				break;
			default:
				throw new Exception("Unrecognized TransactionType for DTO");
		}
		Assertions.assertEquals(msg, e.getMessage());
	}
}
