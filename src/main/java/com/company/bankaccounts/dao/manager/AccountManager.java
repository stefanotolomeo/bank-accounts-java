package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.exceptions.ItemNotFoundException;
import com.company.bankaccounts.dao.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AccountManager extends AbstractManager implements IManager<Account> {

	@Autowired
	private HashOperations<String, String, Account> hashOperations;

	@PostConstruct
	public void initialize(){
		this.CACHE_NAME = Constants.CACHE_ACCOUNT_NAME;
	}


	@Override
	public String save(Account account) throws Exception {
		return null;
	}

	@Override
	public Account update(Account account) throws Exception {
		return null;
	}

	@Override
	public Account findById(String id) {
		return hashOperations.get(CACHE_NAME, id);
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
