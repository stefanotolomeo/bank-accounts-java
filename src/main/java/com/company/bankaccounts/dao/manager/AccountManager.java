package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import com.company.bankaccounts.dao.model.Account;
import com.company.bankaccounts.dao.model.OperationType;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Repository
public class AccountManager extends AbstractManager implements IManager<Account> {

	@Autowired
	private HashOperations<String, String, Account> hashOperations;

	@PostConstruct
	public void initialize() {
		this.CACHE_NAME = Constants.CACHE_ACCOUNT_NAME;
	}

	@Override
	public Account save(Account account) throws Exception {

		validateAccount(OperationType.INSERT, account);

		String nextId = String.valueOf(valueOperations.increment(Constants.INDEX_CACHE_ACCOUNT));
		account.setId(nextId);

		hashOperations.put(CACHE_NAME, nextId, account);
		return findById(nextId);
	}

	void validateAccount(OperationType operationType, Account account) throws InvalidInputException {
		try {
			Preconditions.checkNotNull(account, "Null Account");
			if (operationType == OperationType.UPDATE) {
				Preconditions.checkNotNull(account.getId(), "Null ID");
			}
			Preconditions.checkNotNull(account.getName(), "Null Name");
			Preconditions.checkNotNull(account.getSurname(), "Null Surname");
			Preconditions.checkArgument(account.getPin() != null && !account.getPin().isEmpty(), "Null or Empty Pin");
			if (operationType == OperationType.INSERT) {
				Preconditions.checkNotNull(account.getAmount(), "Null Amount");
			}
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Account: " + e.getMessage());
		}
	}

	// Update involved only Name, Surname, PIN. No updated for the amount - the only way to updated amount is with transactions
	@Override
	public Account update(Account account) throws Exception {

		validateAccount(OperationType.UPDATE, account);

		Account cachedAcc = findById(account.getId());
		if (cachedAcc == null) {
			throw new ItemNotFoundException("Cannot Update: Account ID not found");
		}

		if (account.getAmount().compareTo(cachedAcc.getAmount()) != 0) {
			// ASSUMPTION: The only way to change the amount is with TRANSACTIONS. No manually operations is allowed
			log.warn("Updating Amount is allowed only with TRANSACTION. No changes will be applied to the amount");
			account.setAmount(cachedAcc.getAmount());
		}

		hashOperations.put(CACHE_NAME, account.getId(), account);
		log.info("Updated Account={}", account);

		return account;
	}

	@Override
	public Account findById(String id) throws Exception {
		if(id == null || id.trim().length() == 0){
			throw new InvalidInputException("Invalid Input: Null or empty");
		}

		Account foundAcc = hashOperations.get(CACHE_NAME, id);
		if(foundAcc == null){
			throw new ItemNotFoundException("No Account found for ID="+id);
		}

		return foundAcc;
	}

	@Override
	public Map<String, Account> findAll() {
		return hashOperations.entries(CACHE_NAME);
	}

	@Override
	public Account delete(String id) throws Exception {
		Account a = findById(id);
		if (a == null) {
			throw new ItemNotFoundException("Cannot Delete: Account ID not found");
		}

		hashOperations.delete(CACHE_NAME, a.getId());

		return a;
	}
}
