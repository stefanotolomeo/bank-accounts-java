package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.model.Account;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Repository
public class AccountManager extends AbstractManager implements IManager<Account> {

	@Value("${init.fixture.account.enabled}")
	private boolean initWithFixtures;

	@Value("${init.fixture.account.path}")
	private String PATH;

	@Autowired
	private HashOperations<String, String, Account> hashOperations;

	@PostConstruct
	public void initialize() throws Exception {
		this.DATA_CACHE_NAME = Constants.CACHE_ACCOUNT_NAME;
		this.INDEX_CACHE_NAME = Constants.INDEX_CACHE_ACCOUNT;

		if (initWithFixtures) {
			try(InputStream is = getClass().getResourceAsStream(PATH)){
				List<Account> accountList = new ObjectMapper().readValue(is, new TypeReference<List<Account>>() {});
				for (Account a : accountList) {
					this.save(a);
				}
			}
		}
	}

	@Override
	public Account save(Account account) throws Exception {

		// The Input Account has been previously validated

		String nextId = String.valueOf(valueOperations.increment(INDEX_CACHE_NAME));
		account.setId(nextId);

		hashOperations.put(DATA_CACHE_NAME, nextId, account);
		return findById(nextId);
	}

	// Update involved only Name, Surname, PIN. No updated for the amount - the only way to updated amount is with transactions
	@Override
	public Account update(Account account) throws Exception {

		// The Input Account has been previously validated

		Account cachedAcc = findById(account.getId());
		if (cachedAcc == null) {
			throw new ItemNotFoundException("Cannot Update: Account ID not found");
		}

		// Set the amount to the current one
		account.setAmount(cachedAcc.getAmount());

		hashOperations.put(DATA_CACHE_NAME, account.getId(), account);
		log.info("Updated Account={}", account);

		return account;
	}

	@Override
	public Account findById(String id) throws Exception {
		if (id == null || id.trim().length() == 0) {
			throw new InvalidInputException("Invalid Input: Null or empty");
		}

		Account foundAcc = hashOperations.get(DATA_CACHE_NAME, id);
		if (foundAcc == null) {
			throw new ItemNotFoundException("No Account found for ID=" + id);
		}

		return foundAcc;
	}

	@Override
	public Map<String, Account> findAll() {
		return hashOperations.entries(DATA_CACHE_NAME);
	}

	@Override
	public Account delete(String id) throws Exception {
		Account a = findById(id);
		if (a == null) {
			throw new ItemNotFoundException("Cannot Delete: Account ID not found");
		}

		hashOperations.delete(DATA_CACHE_NAME, a.getId());

		return a;
	}
}
