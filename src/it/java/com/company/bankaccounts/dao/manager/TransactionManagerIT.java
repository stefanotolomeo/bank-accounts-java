package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.dao.model.*;
import com.company.bankaccounts.exceptions.FailedCRUDException;
import com.company.bankaccounts.exceptions.InsufficientAmountException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
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
		transactionHashOperations.put(CACHE_TRANSACTION_NAME, withdrawTransaction.getId(), withdrawTransaction);
		Map<String, AbstractTransaction> res_2 = transactionManager.findAll();
		Assertions.assertNotNull(res_2);
		Assertions.assertFalse(res_2.isEmpty());
		makeAssertionsOnTransactions(withdrawTransaction, res_2.get(withdrawTransaction.getId()));

		// (3) Add the second Transaction (DEPOSIT), then check it
		transactionHashOperations.put(CACHE_TRANSACTION_NAME, depositTransaction.getId(), depositTransaction);
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
		ItemNotFoundException e = Assertions
				.assertThrows(ItemNotFoundException.class, () -> transactionManager.findById(withdrawTransaction.getId()));
		Assertions.assertEquals("No Transaction found for ID=1", e.getMessage());

		// (2) Add one Transaction (WITHDRAW), then check it
		transactionHashOperations.put(CACHE_TRANSACTION_NAME, withdrawTransaction.getId(), withdrawTransaction);
		AbstractTransaction res_2 = transactionManager.findById(withdrawTransaction.getId());
		Assertions.assertNotNull(res_2);
		makeAssertionsOnTransactions(withdrawTransaction, res_2);

	}

	@DisplayName("Delete Test: delete operation is not supported for Transaction")
	@Test
	void delete_Test() {

		FailedCRUDException e = Assertions
				.assertThrows(FailedCRUDException.class, () -> transactionManager.delete(withdrawTransaction.getId()));
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
		TransactionDeposit t2 = new TransactionDeposit(null, BigDecimal.TEN, a1.getId());
		;
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

		t1.setAccountId(a1.getId());    // Save the REAL AccountID for From-AccountID
		ItemNotFoundException e2 = Assertions.assertThrows(ItemNotFoundException.class, () -> transactionManager.save(t1));
		Assertions.assertEquals("Cannot save TRANSFER Transaction: FromAccountID=" + a1.getId() + " or ToAccountId=654 not found",
				e2.getMessage());

		// (1.c) Add To-Account, but failed again: not enough amount into the FROM account
		Account a2 = new Account(null, "name_2", "surname_2", "67890", BigDecimal.valueOf(500));
		accountManager.save(a2);

		t1.setToAccountId(a2.getId());    // Save the REAL AccountID for To-AccountID
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
		Account foundAcc_1 = accountManager.findById(t1.getAccountId());
		Assertions.assertEquals(BigDecimal.valueOf(990), foundAcc_1.getAmount());

		// To-Account: from 500 to 550
		Account foundAcc_2 = accountManager.findById(t1.getToAccountId());
		Assertions.assertEquals(BigDecimal.valueOf(510), foundAcc_2.getAmount());
	}

	@Test
	void saveDepositTransaction_Test() throws Exception {

		TransactionDeposit depositTransaction = new TransactionDeposit("10000", BigDecimal.TEN, "NOT_EXISTING");

		// (1) No account associated to Deposit
		ItemNotFoundException e = Assertions
				.assertThrows(ItemNotFoundException.class, () -> transactionManager.saveDepositTransaction(depositTransaction));
		Assertions.assertEquals("Cannot save DEPOSIT Transaction: AccountID=NOT_EXISTING not found", e.getMessage());

		// (2) Add the account and run Deposit-Transaction

		// (2.1) Add the account and set the transaction's AccountId
		Account a = new Account(null, "Tom", "Smith", "1234", BigDecimal.valueOf(100));
		accountManager.save(a);
		depositTransaction.setAccountId(a.getId());

		// (2.2) Ensure no Deposit-Transaction is present
		Assertions.assertTrue(transactionManager.findAll().isEmpty());

		// (2.3) Run the Deposit-Transaction
		transactionManager.saveDepositTransaction(depositTransaction);

		// (2.4) Assert on Account and Transaction
		Account foundAcc = accountManager.findById(a.getId());
		Assertions.assertNotNull(foundAcc);
		Assertions.assertEquals(BigDecimal.valueOf(110), foundAcc.getAmount());

		Map<String, AbstractTransaction> transMap = transactionManager.findAll();
		Assertions.assertEquals(1, transMap.size());
		TransactionDeposit actualTrans = (TransactionDeposit) transMap.values().iterator().next();
		Assertions.assertEquals(depositTransaction.getAccountId(), actualTrans.getAccountId());
		Assertions.assertEquals(depositTransaction.getAmount(), actualTrans.getAmount());
	}

	@Test
	void saveWithdrawTransaction_Test() throws Exception {

		TransactionWithdraw withdrawTransaction = new TransactionWithdraw("10000", BigDecimal.TEN, "NOT_EXISTING");

		// (1) No account associated to Withdraw
		ItemNotFoundException e = Assertions
				.assertThrows(ItemNotFoundException.class, () -> transactionManager.saveWithdrawTransaction(withdrawTransaction));
		Assertions.assertEquals("Cannot save WITHDRAW Transaction: AccountID=NOT_EXISTING not found", e.getMessage());

		// (2) Add the account and run Deposit-Transaction

		// (2.1) Add the account and set the transaction's AccountId
		Account a = new Account(null, "Tom", "Smith", "1234", BigDecimal.valueOf(5));
		accountManager.save(a);
		withdrawTransaction.setAccountId(a.getId());

		// (2.2) Ensure no Deposit-Transaction is present
		Assertions.assertTrue(transactionManager.findAll().isEmpty());

		// (2.3) Run the Deposit-Transaction: Not enough amount
		InsufficientAmountException e2 = Assertions
				.assertThrows(InsufficientAmountException.class, () -> transactionManager.saveWithdrawTransaction(withdrawTransaction));
		Assertions.assertEquals("Not enough amount", e2.getMessage());

		// (2.4) Decrease the Withdraw amount and retry
		withdrawTransaction.setAmount(BigDecimal.valueOf(5));
		transactionManager.saveWithdrawTransaction(withdrawTransaction);

		// (2.5) Assert on Account and Transaction
		Account foundAcc = accountManager.findById(a.getId());
		Assertions.assertNotNull(foundAcc);
		Assertions.assertEquals(BigDecimal.valueOf(0), foundAcc.getAmount());

		Map<String, AbstractTransaction> transMap = transactionManager.findAll();
		Assertions.assertEquals(1, transMap.size());
		TransactionWithdraw actualTrans = (TransactionWithdraw) transMap.values().iterator().next();
		Assertions.assertEquals(withdrawTransaction.getAccountId(), actualTrans.getAccountId());
		Assertions.assertEquals(withdrawTransaction.getAmount(), actualTrans.getAmount());
	}

	@Test
	void saveTransferTransaction_Test() throws Exception {

		TransactionTransfer transferTransaction = new TransactionTransfer("10000", BigDecimal.TEN, "NOT_EXISTING_FROM", "NOT_EXISTING_TO");

		// (1) No FROM-account associated to Transfer
		ItemNotFoundException e1 = Assertions
				.assertThrows(ItemNotFoundException.class, () -> transactionManager.saveTransferTransaction(transferTransaction));
		Assertions
				.assertEquals("Cannot save TRANSFER Transaction: FromAccountID=NOT_EXISTING_FROM or ToAccountId=NOT_EXISTING_TO not found",
						e1.getMessage());

		// (2) Add the accounts and run Deposit-Transaction

		// (2.1) Add the FROM-account and set the transaction's AccountId
		Account accFrom = new Account(null, "Tom", "Smith", "1234", BigDecimal.valueOf(5));
		accountManager.save(accFrom);
		transferTransaction.setAccountId(accFrom.getId());

		// (2.2) No TO-account associated to Transfer
		ItemNotFoundException e2 = Assertions
				.assertThrows(ItemNotFoundException.class, () -> transactionManager.saveTransferTransaction(transferTransaction));
		Assertions.assertEquals(
				"Cannot save TRANSFER Transaction: FromAccountID=" + accFrom.getId() + " or ToAccountId=NOT_EXISTING_TO not found",
				e2.getMessage());

		// (2.3) Add the TO-account and set the transaction's AccountId
		Account accTo = new Account(null, "Rick", "Red", "5678", BigDecimal.valueOf(10));
		accountManager.save(accTo);
		transferTransaction.setToAccountId(accTo.getId());

		// (2.4) Ensure no Transfer-Transaction is present
		Assertions.assertTrue(transactionManager.findAll().isEmpty());

		// (2.5) Run the Transfer-Transaction: Not enough amount
		InsufficientAmountException e3 = Assertions
				.assertThrows(InsufficientAmountException.class, () -> transactionManager.saveTransferTransaction(transferTransaction));
		Assertions.assertEquals("Not enough amount", e3.getMessage());

		// (2.5) Decrease the Transfer amount and retry
		transferTransaction.setAmount(BigDecimal.valueOf(5));
		transactionManager.saveTransferTransaction(transferTransaction);

		// (2.5) Assert on Accounts and Transaction
		Account foundFromAcc = accountManager.findById(accFrom.getId());
		Assertions.assertNotNull(foundFromAcc);
		Assertions.assertEquals(BigDecimal.valueOf(0), foundFromAcc.getAmount());

		Account foundToAcc = accountManager.findById(accTo.getId());
		Assertions.assertNotNull(foundToAcc);
		Assertions.assertEquals(BigDecimal.valueOf(15), foundToAcc.getAmount());

		Map<String, AbstractTransaction> transMap = transactionManager.findAll();
		Assertions.assertEquals(1, transMap.size());
		TransactionTransfer actualTrans = (TransactionTransfer) transMap.values().iterator().next();
		Assertions.assertEquals(transferTransaction.getAccountId(), actualTrans.getAccountId());
		Assertions.assertEquals(transferTransaction.getToAccountId(), actualTrans.getToAccountId());
		Assertions.assertEquals(transferTransaction.getAmount(), actualTrans.getAmount());
	}
}
