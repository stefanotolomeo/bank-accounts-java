package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.exceptions.FailedCRUDException;
import com.company.bankaccounts.dao.exceptions.InsufficientAmountException;
import com.company.bankaccounts.dao.exceptions.InvalidInputException;
import com.company.bankaccounts.dao.exceptions.ItemNotFoundException;
import com.company.bankaccounts.dao.model.*;
import com.company.bankaccounts.itconfig.BaseIT;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

class TransactionManagerIT extends BaseIT {

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private AccountManager accountManager;

	// *** WITHDRAW Transaction *** //
	private final String id_1 = "1";
	private final BigDecimal amount_1 = BigDecimal.valueOf(100);
	private final String accountId_1 = "111";
	private TransactionWithdraw withdrawTransaction = new TransactionWithdraw(id_1, amount_1, accountId_1);

	// *** DEPOSIT Transaction *** //
	private final String id_2 = "2";
	private final BigDecimal amount_2 = BigDecimal.valueOf(150);
	private final String accountId_2 = "222";
	private TransactionDeposit depositTransaction = new TransactionDeposit(id_2, amount_2, accountId_2);

	// *** TRANSFER Transaction *** //
	private final String id_3 = "3";
	private final BigDecimal amount_3 = BigDecimal.valueOf(200);
	private final String fromAccountId_3 = "333";
	private final String toAccountId_3 = "444";
	private TransactionTransfer transferTransaction = new TransactionTransfer(id_3, amount_3, fromAccountId_3, toAccountId_3);

	@BeforeEach
	void setup() {
		clearAllCaches();
	}

	@AfterEach
	void tearDown() {
		clearAllCaches();
	}

	@DisplayName("FindAll Test: retrieve all Transactions into cache")
	@Test
	void findAll_Test() throws Exception {

		// (1) Empty map
		Map<String, AbstractTransaction> res_1 = transactionManager.findAll();
		Assertions.assertNotNull(res_1);
		Assertions.assertTrue(res_1.isEmpty());

		// (2) Add one Transaction (WITHDRAW), then check it
		transactionHashOperations.put(Constants.CACHE_TRANSACTION_NAME, withdrawTransaction.getId(), withdrawTransaction);
		Map<String, AbstractTransaction> res_2 = transactionManager.findAll();
		Assertions.assertNotNull(res_2);
		Assertions.assertFalse(res_2.isEmpty());
		makeAssertionsOnTransactions(withdrawTransaction, res_2.get(withdrawTransaction.getId()));

		// (3) Add the second Transaction (DEPOSIT), then check it
		transactionHashOperations.put(Constants.CACHE_TRANSACTION_NAME, depositTransaction.getId(), depositTransaction);
		Map<String, AbstractTransaction> res_3 = transactionManager.findAll();
		Assertions.assertNotNull(res_3);
		Assertions.assertEquals(2, res_3.size());
		makeAssertionsOnTransactions(withdrawTransaction, res_3.get(withdrawTransaction.getId()));
		makeAssertionsOnTransactions(depositTransaction, res_3.get(depositTransaction.getId()));
	}

	@DisplayName("FindByID Test: retrieve a Transaction by ID")
	@Test
	void findBy_Test() throws Exception {

		// (1) Empty map
		AbstractTransaction res_1 = transactionManager.findById(withdrawTransaction.getId());
		Assertions.assertNull(res_1);

		// (2) Add one Transaction (WITHDRAW), then check it
		transactionHashOperations.put(Constants.CACHE_TRANSACTION_NAME, withdrawTransaction.getId(), withdrawTransaction);
		AbstractTransaction res_2 = transactionManager.findById(withdrawTransaction.getId());
		Assertions.assertNotNull(res_2);
		makeAssertionsOnTransactions(withdrawTransaction, res_2);

	}

	@DisplayName("Delete Test: delete operation is not supported for Transaction")
	@Test
	void delete_Test() {

		FailedCRUDException e = Assertions.assertThrows(FailedCRUDException.class, () -> transactionManager.delete(withdrawTransaction.getId()));
		Assertions.assertEquals(OperationType.DELETE, e.getOperation());
		Assertions.assertEquals("Unsupported operation: cannot manually delete a TRANSACTION record", e.getMessage());

	}

	@DisplayName("Update Test: update operation is not supported for Transaction")
	@Test
	void update_Test() {

		FailedCRUDException e = Assertions.assertThrows(FailedCRUDException.class, () -> transactionManager.update(withdrawTransaction));
		Assertions.assertEquals(OperationType.UPDATE, e.getOperation());
		Assertions.assertEquals("Unsupported operation: cannot manually update an TRANSACTION record", e.getMessage());

	}

	@DisplayName("Save Test: save a new WITHDRAW Transaction")
	@Test
	void save_withdraw_Test() throws Exception {

		// (0) Skip test on validations (multiple dedicated test methods exists)

		// (1) Test for WITHDRAW Transaction

		// (1.a) Test without account: exception expected
		TransactionWithdraw t1 = new TransactionWithdraw(null, BigDecimal.TEN, "987");
		ItemNotFoundException e1 = Assertions.assertThrows(ItemNotFoundException.class, () -> transactionManager.save(t1));
		Assertions.assertEquals("Cannot save WITHDRAW Transaction: AccountID=987 not found", e1.getMessage());

		// (1.b) Add the related Account. Execute a Withdraw Transaction not allowed due to insufficient amount
		BigDecimal startAmount = BigDecimal.valueOf(1000);
		Account a1 = new Account(null, "name_1", "surname_1", "12345", startAmount);
		accountManager.save(a1);

		BigDecimal requestedAmount = BigDecimal.valueOf(1001);
		TransactionWithdraw t2 = new TransactionWithdraw(null, requestedAmount, a1.getId());
		InsufficientAmountException e2 = Assertions.assertThrows(InsufficientAmountException.class, () -> transactionManager.save(t2));
		Assertions.assertEquals("Not enough amount", e2.getMessage());
		Assertions.assertEquals(startAmount, e2.getAvailableFunds());
		Assertions.assertEquals(requestedAmount, e2.getRequestedFunds());

		// (1.c) Execute an allowed Transaction and ensure the related Account's amount decreased
		TransactionWithdraw t3 = new TransactionWithdraw(null, BigDecimal.TEN, a1.getId());
		AbstractTransaction res_3 = transactionManager.save(t3);
		Assertions.assertNotNull(res_3);
		makeAssertionsOnTransactions(t3, res_3);

		Account foundAcc_1 = accountManager.findById(a1.getId());
		Assertions.assertEquals(BigDecimal.valueOf(990), foundAcc_1.getAmount());

	}

	@DisplayName("Save Test: save a new DEPOSIT Transaction")
	@Test
	void save_deposit_Test() throws Exception {

		// (0) Skip test on validations (multiple dedicated test methods exists)

		// (1) Test for DEPOSIT Transaction

		// (1.a) Test without account: exception expected
		TransactionDeposit t1 = new TransactionDeposit(null, BigDecimal.TEN, "987");
		ItemNotFoundException e1 = Assertions.assertThrows(ItemNotFoundException.class, () -> transactionManager.save(t1));
		Assertions.assertEquals("Cannot save DEPOSIT Transaction: AccountID=987 not found", e1.getMessage());

		// (1.b) Add the related Account
		BigDecimal startAmount = BigDecimal.valueOf(1000);
		Account a1 = new Account(null, "name_1", "surname_1", "12345", startAmount);
		accountManager.save(a1);

		// (1.c) Execute an allowed Transaction and ensure the related Account's amount decreased
		TransactionDeposit t2 = new TransactionDeposit(null, BigDecimal.TEN, a1.getId());;
		AbstractTransaction res_2 = transactionManager.save(t2);
		Assertions.assertNotNull(res_2);
		makeAssertionsOnTransactions(t2, res_2);

		Account foundAcc_1 = accountManager.findById(a1.getId());
		Assertions.assertEquals(BigDecimal.valueOf(1010), foundAcc_1.getAmount());

	}

	@DisplayName("Save Test: save a new TRANSFER Transaction")
	@Test
	void save_transfer_Test() throws Exception {

		// (0) Skip test on validations (multiple dedicated test methods exists)

		// (1) Test for TRANSFER Transaction

		// (1.a) Test without account: exception expected
		BigDecimal requestedAmount = BigDecimal.valueOf(1001);
		TransactionTransfer t1 = new TransactionTransfer(null, requestedAmount, "987", "654");
		ItemNotFoundException e1 = Assertions.assertThrows(ItemNotFoundException.class, () -> transactionManager.save(t1));
		Assertions.assertEquals("Cannot save TRANSFER Transaction: FromAccountID=987 or ToAccountId=654 not found", e1.getMessage());

		// (1.b) Add From-Account, but failed since To-Account is missing
		Account a1 = new Account(null, "name_1", "surname_1", "12345", BigDecimal.valueOf(1000));
		accountManager.save(a1);

		t1.setFromAccountId(a1.getId());	// Save the REAL AccountID for From-AccountID
		ItemNotFoundException e2 = Assertions.assertThrows(ItemNotFoundException.class, () -> transactionManager.save(t1));
		Assertions.assertEquals("Cannot save TRANSFER Transaction: FromAccountID="+a1.getId()+" or ToAccountId=654 not found", e2.getMessage());

		// (1.c) Add To-Account, but failed again: not enough amount into the FROM account
		Account a2 = new Account(null, "name_2", "surname_2", "67890", BigDecimal.valueOf(500));
		accountManager.save(a2);

		t1.setToAccountId(a2.getId());	// Save the REAL AccountID for To-AccountID
		InsufficientAmountException e3 = Assertions.assertThrows(InsufficientAmountException.class, () -> transactionManager.save(t1));
		Assertions.assertEquals("Not enough amount", e3.getMessage());
		Assertions.assertEquals(BigDecimal.valueOf(1000), e3.getAvailableFunds());
		Assertions.assertEquals(requestedAmount, e3.getRequestedFunds());

		// (1.d) Decrease the Transaction Amount to allow the transaction and check the accounts' amounts
		t1.setAmount(BigDecimal.TEN);
		AbstractTransaction res_1 = transactionManager.save(t1);
		Assertions.assertNotNull(res_1);
		makeAssertionsOnTransactions(t1, res_1);

		// From-Account: from 1000 to 990
		Account foundAcc_1 = accountManager.findById(t1.getFromAccountId());
		Assertions.assertEquals(BigDecimal.valueOf(990), foundAcc_1.getAmount());

		// To-Account: from 500 to 550
		Account foundAcc_2 = accountManager.findById(t1.getToAccountId());
		Assertions.assertEquals(BigDecimal.valueOf(510), foundAcc_2.getAmount());
	}

	@DisplayName("Validation of WITHDRAW Transaction")
	@Test
	void validateTransaction_withdraw_Test() throws InvalidInputException {

		// (1) Invalid Transaction: null
		validateAndAssertException(null, "Invalid Transaction: Null Transaction");

		// (2) Invalid Transaction: null Amount
		TransactionWithdraw invalidTransaction = new TransactionWithdraw(null, null, null);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null Amount");

		// (3) Invalid Transaction: negative Amount
		invalidTransaction.setAmount(BigDecimal.valueOf(-12));
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (4) Invalid Transaction: zero Amount
		invalidTransaction.setAmount(BigDecimal.ZERO);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (5) Invalid Transaction: null AccountID
		invalidTransaction.setAmount(BigDecimal.TEN);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (6) Invalid Transaction: empty AccountID
		invalidTransaction.setAccountId("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (7) Valid Transaction: no exception here
		invalidTransaction.setAccountId("123");
		transactionManager.validateTransaction(invalidTransaction);
	}

	@DisplayName("Validation of DEPOSIT Transaction")
	@Test
	void validateTransaction_deposit_Test() throws InvalidInputException {

		// (1) Invalid Transaction: null
		validateAndAssertException(null, "Invalid Transaction: Null Transaction");

		// (2) Invalid Transaction: null Amount
		TransactionDeposit invalidTransaction = new TransactionDeposit(null, null, null);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null Amount");

		// (3) Invalid Transaction: negative Amount
		invalidTransaction.setAmount(BigDecimal.valueOf(-12));
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (4) Invalid Transaction: zero Amount
		invalidTransaction.setAmount(BigDecimal.ZERO);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (5) Invalid Transaction: null AccountID
		invalidTransaction.setAmount(BigDecimal.TEN);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (6) Invalid Transaction: empty AccountID
		invalidTransaction.setAccountId("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty AccountID");

		// (7) Valid Transaction: no exception here
		invalidTransaction.setAccountId("123");
		transactionManager.validateTransaction(invalidTransaction);
	}

	@Test
	void validateTransaction_transfer_Test() throws InvalidInputException {
		// (1) Invalid Transaction: null
		validateAndAssertException(null, "Invalid Transaction: Null Transaction");

		// (2) Invalid Transaction: null Amount
		TransactionTransfer invalidTransaction = new TransactionTransfer(null, null, null, null);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null Amount");

		// (3) Invalid Transaction: negative Amount
		invalidTransaction.setAmount(BigDecimal.valueOf(-12));
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (4) Invalid Transaction: zero Amount
		invalidTransaction.setAmount(BigDecimal.ZERO);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Not Positive Amount");

		// (5) Invalid Transaction: null From-AccountID
		invalidTransaction.setAmount(BigDecimal.TEN);
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty From-AccountID");

		// (6) Invalid Transaction: empty From-AccountID
		invalidTransaction.setFromAccountId("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty From-AccountID");

		// (7) Invalid Transaction: null To-AccountID
		invalidTransaction.setFromAccountId("123");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty To-AccountID");

		// (8) Invalid Transaction: empty To-AccountID
		invalidTransaction.setToAccountId("");
		validateAndAssertException(invalidTransaction, "Invalid Transaction: Null or Empty To-AccountID");

		// (9) Valid Transaction: no exception here
		invalidTransaction.setToAccountId("456");
		transactionManager.validateTransaction(invalidTransaction);

	}

	private void validateAndAssertException(AbstractTransaction trans, String msg){
		InvalidInputException e = Assertions
				.assertThrows(InvalidInputException.class, () -> transactionManager.validateTransaction(trans));
		Assertions.assertEquals(msg, e.getMessage());
	}
}
