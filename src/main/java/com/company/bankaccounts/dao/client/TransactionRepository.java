package com.company.bankaccounts.dao.client;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.model.AbstractTransaction;
import com.company.bankaccounts.dao.model.Account;
import com.company.bankaccounts.dao.model.OperationType;
import com.company.bankaccounts.exceptions.FailedCRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionRepository {

	private String DATA_CACHE_NAME;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private HashOperations<String, String, AbstractTransaction> hashOperations;

	@PostConstruct
	public void initialize() {
		this.DATA_CACHE_NAME = Constants.CACHE_TRANSACTION_NAME;
		hashOperations = redisTemplate.opsForHash();
	}

	public void save(AbstractTransaction newTransaction, List<Account> updatedAccountList) throws FailedCRUDException {
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

	public AbstractTransaction getById(String id) {
		return hashOperations.get(DATA_CACHE_NAME, id);
	}

	public Map<String, AbstractTransaction> getAll() {
		return hashOperations.entries(DATA_CACHE_NAME);
	}
}
