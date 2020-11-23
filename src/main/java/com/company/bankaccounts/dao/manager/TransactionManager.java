package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.exceptions.FailedCRUDException;
import com.company.bankaccounts.dao.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class TransactionManager extends AbstractManager implements IManager<AbstractTransaction> {

	@Autowired
	private HashOperations<String, String, AbstractTransaction> hashOperations;

	@Autowired
	private TransactionalExecutor transactionalExecutor;

	@PostConstruct
	public void initialize() {
		this.CACHE_NAME = Constants.CACHE_TRANSACTION_NAME;
	}

	@Override
	public AbstractTransaction save(AbstractTransaction item) throws Exception {

		// TODO: Validate input
		// TODO: Check PIN: put a layer at the top of this method
		String nextTransactionId = String.valueOf(valueOperations.increment(Constants.INDEX_CACHE_ACCOUNT));
		item.setId(nextTransactionId);

		switch (item.getTransactionType()){
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

	@Override
	public AbstractTransaction update(AbstractTransaction item) throws Exception {
		// ASSUMPTION: Cannot UPDATE a transaction. It will bring the involved accounts in an INCONSISTENT status.
		throw new FailedCRUDException(OperationType.UPDATE, "Unsupported operation: cannot manually update an TRANSACTION record");
	}

	@Override
	public AbstractTransaction findById(String id) {
		return hashOperations.get(CACHE_NAME, id);
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
