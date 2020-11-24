package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.exceptions.FailedCRUDException;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import com.company.bankaccounts.dao.logic.TransactionalExecutor;
import com.company.bankaccounts.dao.model.*;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;

@Repository
public class TransactionManager extends AbstractManager implements IManager<AbstractTransaction> {

	@Autowired
	private HashOperations<String, String, AbstractTransaction> hashOperations;

	@Autowired
	private TransactionalExecutor transactionalExecutor;

	@PostConstruct
	public void initialize() {
		this.CACHE_NAME = Constants.CACHE_TRANSACTION_NAME;
	}

	// At this layer, the PIN has been checked yet
	@Override
	public AbstractTransaction save(AbstractTransaction item) throws Exception {

		validateTransaction(item);

		String nextTransactionId = String.valueOf(valueOperations.increment(Constants.INDEX_CACHE_ACCOUNT));
		item.setId(nextTransactionId);

		switch (item.getTransactionType()) {
		case WITHDRAW:
			TransactionWithdraw withdrawTrans = (TransactionWithdraw) item;
			transactionalExecutor.saveWithdrawTransaction(withdrawTrans);
			break;
		case DEPOSIT:
			TransactionDeposit depositTrans = (TransactionDeposit) item;
			transactionalExecutor.saveDepositTransaction(depositTrans);
			break;
		case TRANSFER:
			TransactionTransfer trasferTrans = (TransactionTransfer) item;
			transactionalExecutor.saveTransferTransaction(trasferTrans);
			break;
		}

		return findById(nextTransactionId);
	}

	void validateTransaction(AbstractTransaction transaction) throws InvalidInputException {

		try {
			Preconditions.checkNotNull(transaction, "Null Transaction");
			Preconditions.checkNotNull(transaction.getAmount(), "Null Amount");
			Preconditions.checkArgument(transaction.getAmount().compareTo(BigDecimal.ZERO) > 0, "Not Positive Amount");
			if (transaction.getTransactionType() == TransactionType.WITHDRAW) {
				TransactionWithdraw withdraw = (TransactionWithdraw) transaction;
				Preconditions
						.checkArgument(withdraw.getAccountId() != null && !withdraw.getAccountId().isEmpty(), "Null or Empty AccountID");
			} else if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
				TransactionDeposit deposit = (TransactionDeposit) transaction;
				Preconditions.checkArgument(deposit.getAccountId() != null && !deposit.getAccountId().isEmpty(), "Null or Empty AccountID");
			} else if (transaction.getTransactionType() == TransactionType.TRANSFER) {
				TransactionTransfer transfer = (TransactionTransfer) transaction;
				Preconditions.checkArgument(transfer.getAccountId() != null && !transfer.getAccountId().isEmpty(),
						"Null or Empty From-AccountID");
				Preconditions.checkArgument(transfer.getToAccountId() != null && !transfer.getToAccountId().isEmpty(),
						"Null or Empty To-AccountID");
			} else {
				throw new Exception("Invalid TransactionType");
			}
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Transaction: " + e.getMessage());
		}

	}

	@Override
	public AbstractTransaction update(AbstractTransaction item) throws Exception {
		// ASSUMPTION: Cannot UPDATE a transaction. It will bring the involved accounts in an INCONSISTENT status.
		throw new FailedCRUDException(OperationType.UPDATE, "Unsupported operation: cannot manually update an TRANSACTION record");
	}

	@Override
	public AbstractTransaction findById(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new InvalidInputException("Invalid Input: Null or empty");
		}

		AbstractTransaction foundTrans = hashOperations.get(CACHE_NAME, id);
		if (foundTrans == null) {
			throw new ItemNotFoundException("No Transaction found for ID=" + id);
		}

		return foundTrans;
	}

	@Override
	public Map<String, AbstractTransaction> findAll() {
		return hashOperations.entries(CACHE_NAME);
	}

	@Override
	public AbstractTransaction delete(String id) throws Exception {
		// ASSUMPTION: Cannot DELETE a transaction. It will bring the involved accounts in an INCONSISTENT status.
		throw new FailedCRUDException(OperationType.DELETE, "Unsupported operation: cannot manually delete a TRANSACTION record");
	}
}
