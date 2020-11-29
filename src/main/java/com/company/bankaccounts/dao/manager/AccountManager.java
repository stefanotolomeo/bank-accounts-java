package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.dao.client.AccountRepository;
import com.company.bankaccounts.dao.client.IndexRepository;
import com.company.bankaccounts.dao.model.Account;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class AccountManager extends AbstractManager implements IManager<Account> {

	@Value("${init.fixture.account.enabled}")
	private boolean initWithFixtures;

	@Value("${init.fixture.account.path}")
	private String PATH;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private IndexRepository indexRepository;

	@PostConstruct
	public void initialize() throws Exception {
		if (initWithFixtures) {
			try (InputStream is = getClass().getResourceAsStream(PATH)) {
				List<Account> accountList = new ObjectMapper().readValue(is, new TypeReference<List<Account>>() {
				});
				for (Account a : accountList) {
					this.save(a);
				}
			}
		}
	}

	@Override
	public Account save(Account account) throws Exception {

		// The Input Account has been previously validated

		String nextId = indexRepository.getNextIdForAccount();
		account.setId(nextId);

		accountRepository.save(account);

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

		accountRepository.update(account);

		log.info("Updated Account={}", account);

		return account;
	}

	@Override
	public Account findById(String id) throws Exception {
		if (id == null || id.trim().length() == 0) {
			throw new InvalidInputException("Invalid Input: Null or empty");
		}

		Account foundAcc = accountRepository.getById(id);
		if (foundAcc == null) {
			throw new ItemNotFoundException("No Account found for ID=" + id);
		}

		return foundAcc;
	}

	@Override
	public Map<String, Account> findAll() {

		return accountRepository.getAll();
	}

	@Override
	public Account delete(String id) throws Exception {
		Account a = findById(id);
		if (a == null) {
			throw new ItemNotFoundException("Cannot Delete: Account ID not found");
		}

		accountRepository.delete(id);

		return a;
	}
}
