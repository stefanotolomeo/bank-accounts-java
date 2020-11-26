package com.company.bankaccounts.dao.logic;

import com.company.bankaccounts.dao.manager.AccountManager;
import com.company.bankaccounts.dao.manager.TransactionManager;
import com.company.bankaccounts.dao.model.*;
import com.company.bankaccounts.exceptions.InsufficientAmountException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import com.company.bankaccounts.itconfig.BaseIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

class TransactionalExecutorIT extends BaseIT {

	@Autowired
	private TransactionalExecutor transactionalExecutor;

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private TransactionManager transactionManager;

	@BeforeEach
	void setup() {
		clearAllCaches();
	}

	@AfterEach
	void tearDown() {
		clearAllCaches();
	}

	@Test
	void saveDepositTransaction_Test() throws Exception {

		TransactionDeposit depositTransaction = new TransactionDeposit("10000", BigDecimal.TEN, "NOT_EXISTING");

		// (1) No account associated to Deposit
		ItemNotFoundException e = Assertions
				.assertThrows(ItemNotFoundException.class, () -> transactionalExecutor.saveDepositTransaction(depositTransaction));
		Assertions.assertEquals("Cannot save DEPOSIT Transaction: AccountID=NOT_EXISTING not found", e.getMessage());

		// (2) Add the account and run Deposit-Transaction

		// (2.1) Add the account and set the transaction's AccountId
		Account a = new Account(null, "Tom", "Smith", "1234", BigDecimal.valueOf(100));
		accountManager.save(a);
		depositTransaction.setAccountId(a.getId());

		// (2.2) Ensure no Deposit-Transaction is present
		Assertions.assertTrue(transactionManager.findAll().isEmpty());

		// (2.3) Run the Deposit-Transaction
		transactionalExecutor.saveDepositTransaction(depositTransaction);

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
				.assertThrows(ItemNotFoundException.class, () -> transactionalExecutor.saveWithdrawTransaction(withdrawTransaction));
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
				.assertThrows(InsufficientAmountException.class, () -> transactionalExecutor.saveWithdrawTransaction(withdrawTransaction));
		Assertions.assertEquals("Not enough amount", e2.getMessage());

		// (2.4) Decrease the Withdraw amount and retry
		withdrawTransaction.setAmount(BigDecimal.valueOf(5));
		transactionalExecutor.saveWithdrawTransaction(withdrawTransaction);

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
				.assertThrows(ItemNotFoundException.class, () -> transactionalExecutor.saveTransferTransaction(transferTransaction));
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
				.assertThrows(ItemNotFoundException.class, () -> transactionalExecutor.saveTransferTransaction(transferTransaction));
		Assertions
				.assertEquals("Cannot save TRANSFER Transaction: FromAccountID="+accFrom.getId()+" or ToAccountId=NOT_EXISTING_TO not found",
						e2.getMessage());

		// (2.3) Add the TO-account and set the transaction's AccountId
		Account accTo = new Account(null, "Rick", "Red", "5678", BigDecimal.valueOf(10));
		accountManager.save(accTo);
		transferTransaction.setToAccountId(accTo.getId());

		// (2.4) Ensure no Transfer-Transaction is present
		Assertions.assertTrue(transactionManager.findAll().isEmpty());

		// (2.5) Run the Transfer-Transaction: Not enough amount
		InsufficientAmountException e3 = Assertions
				.assertThrows(InsufficientAmountException.class, () -> transactionalExecutor.saveTransferTransaction(transferTransaction));
		Assertions.assertEquals("Not enough amount", e3.getMessage());

		// (2.5) Decrease the Transfer amount and retry
		transferTransaction.setAmount(BigDecimal.valueOf(5));
		transactionalExecutor.saveTransferTransaction(transferTransaction);

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
