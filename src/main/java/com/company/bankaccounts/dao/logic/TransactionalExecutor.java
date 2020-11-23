package com.company.bankaccounts.dao.logic;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.exceptions.FailedCRUDException;
import com.company.bankaccounts.dao.exceptions.InsufficientAmountException;
import com.company.bankaccounts.dao.exceptions.ItemNotFoundException;
import com.company.bankaccounts.dao.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Used for execute TRANSACTIONAL operations between TRANSACTION and ACCOUNT
@Service
public class TransactionalExecutor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HashOperations<String, String, AbstractTransaction> transactionHashOperations;

	@Autowired
	private HashOperations<String, String, Account> accountHashOperations;

	// Useful to manage Transaction Operations
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public void saveWithdrawTransaction(TransactionWithdraw transactionWithdraw) throws Exception {

		Account acc = accountHashOperations.get(Constants.CACHE_ACCOUNT_NAME, transactionWithdraw.getAccountId());

		if (acc == null) {
			String msg = String.format("Cannot save WITHDRAW Transaction: AccountID=%s not found", transactionWithdraw.getAccountId());
			throw new ItemNotFoundException(msg);
		}

		BigDecimal newAmount = acc.getAmount().subtract(transactionWithdraw.getAmount());

		if(newAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new InsufficientAmountException("Not enough amount", acc.getAmount(), transactionWithdraw.getAmount());
		}
		log.info("WITHDRAW Transaction ALLOWED: oldAmount={}, transactionAmount={}, newAmount={}", acc.getAmount(), transactionWithdraw.getAmount(),
				newAmount);
		acc.setAmount(newAmount);
		makeTransactionalInsert(transactionWithdraw, Collections.singletonList(acc));
	}

	public void saveDepositTransaction(TransactionDeposit transactionDeposit) throws Exception {

		Account acc = accountHashOperations.get(Constants.CACHE_ACCOUNT_NAME, transactionDeposit.getAccountId());

		if (acc == null) {
			String msg = String.format("Cannot save DEPOSIT Transaction: AccountID=%s not found", transactionDeposit.getAccountId());
			throw new ItemNotFoundException(msg);
		}

		BigDecimal newAmount = acc.getAmount().add(transactionDeposit.getAmount());

		log.info("DEPOSIT Transaction: oldAmount={}, transactionAmount={}, newAmount={}", acc.getAmount(), transactionDeposit.getAmount(),
				newAmount);

		acc.setAmount(newAmount);
		makeTransactionalInsert(transactionDeposit, Collections.singletonList(acc));
	}

	public void saveTransferTransaction(TransactionTransfer transactionTransfer) throws Exception {

		Account fromAccount = accountHashOperations.get(Constants.CACHE_ACCOUNT_NAME, transactionTransfer.getFromAccountId());
		Account toAccount = accountHashOperations.get(Constants.CACHE_ACCOUNT_NAME, transactionTransfer.getToAccountId());

		if (fromAccount == null || toAccount == null) {
			String msg = String.format("Cannot save TRANSFER Transaction: FromAccountID=%s or ToAccountId=%s not found",
					transactionTransfer.getFromAccountId(), transactionTransfer.getToAccountId());
			throw new ItemNotFoundException(msg);
		}

		BigDecimal fromAcc_newAmount = fromAccount.getAmount().subtract(transactionTransfer.getAmount());
		BigDecimal toAcc_newAmount = toAccount.getAmount().add(transactionTransfer.getAmount());

		if(fromAcc_newAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new InsufficientAmountException("Not enough amount", fromAccount.getAmount(), transactionTransfer.getAmount());
		}

		log.info("TRANSFER Transaction: Account_FROM has oldAmount={}, transactionAmount={}, newAmount={}; "
						+ "Account_TO has oldAmount={}, transactionAmount={}, newAmount={};", fromAccount.getAmount(),
				transactionTransfer.getAmount(), fromAcc_newAmount, toAccount.getAmount(), transactionTransfer.getAmount(),
				toAcc_newAmount);
		fromAccount.setAmount(fromAcc_newAmount);
		toAccount.setAmount(toAcc_newAmount);
		makeTransactionalInsert(transactionTransfer, Arrays.asList(fromAccount, toAccount));
	}

	private void makeTransactionalInsert(AbstractTransaction newTransaction, List<Account> updatedAccountList) throws FailedCRUDException {

		//execute a Transactional operation: This will contain the results of all operations in the transaction
		List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
			public List<Object> execute(RedisOperations operations) throws DataAccessException {

				// (1) Start Transactional operation
				operations.multi();

				// (2) Execute the operations:
				// (2.1) Save the transaction
				operations.opsForHash().put(Constants.CACHE_TRANSACTION_NAME, newTransaction.getId(), newTransaction);
				// (2.2) Decrease the amount from the involved accounts
				for (Account a : updatedAccountList) {
					operations.opsForHash().put(Constants.CACHE_ACCOUNT_NAME, a.getId(), a);
				}

				// (3) Execute operations
				return operations.exec();
			}
		});

		if (txResults == null || txResults.size() == 0) {
			String msg = String.format("Cannot save Item: error while inserting into cache. TxResults is %s", txResults);
			throw new FailedCRUDException(OperationType.INSERT, msg);
		}
	}

}
