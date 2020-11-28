package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.model.Account;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import com.company.bankaccounts.itconfig.BaseIT;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

class AccountManagerIT extends BaseIT {

	@Autowired
	private AccountManager accountManager;

	private final String id_1 = "1";
	private final String id_2 = "2";

	private final String name_1 = "name_1";
	private final String surname_1 = "surname_1";
	private final String pin_1 = "12345";
	private final BigDecimal amount_1 = BigDecimal.valueOf(1000);

	private final String name_2 = "name_2";
	private final String surname_2 = "surname_2";
	private final String pin_2 = "67890";
	private final BigDecimal amount_2 = BigDecimal.valueOf(2000);

	private final Account a1 = new Account(id_1, name_1, surname_1, pin_1, amount_1);
	private final Account a2 = new Account(id_2, name_2, surname_2, pin_2, amount_2);

	@BeforeEach
	void setup() {
		clearAllCaches();
	}

	@AfterEach
	void tearDown() {
		clearAllCaches();
	}

	@DisplayName("FindAll Test: retrieve all Accounts into cache")
	@Test
	void findAll_Test() {

		// (1) Empty map
		Map<String, Account> res_1 = accountManager.findAll();
		Assertions.assertNotNull(res_1);
		Assertions.assertTrue(res_1.isEmpty());

		// (2) Add one account, then check it
		accountHashOperations.put(Constants.CACHE_ACCOUNT_NAME, a1.getId(), a1);
		Map<String, Account> res_2 = accountManager.findAll();
		Assertions.assertNotNull(res_2);
		Assertions.assertFalse(res_2.isEmpty());
		makeAssertionsOnAccounts(a1, res_2.get(a1.getId()));

		// (3) Add the second Account, then check it
		accountHashOperations.put(Constants.CACHE_ACCOUNT_NAME, a2.getId(), a2);
		Map<String, Account> res_3 = accountManager.findAll();
		Assertions.assertNotNull(res_3);
		Assertions.assertEquals(2, res_3.size());
		makeAssertionsOnAccounts(a1, res_3.get(a1.getId()));
		makeAssertionsOnAccounts(a2, res_3.get(a2.getId()));
	}

	@DisplayName("FindByID Test: retrieve an Account by ID")
	@Test
	void findBy_Test() throws Exception {

		// (1) Empty map
		ItemNotFoundException e = Assertions.assertThrows(ItemNotFoundException.class, () -> accountManager.findById(a1.getId()));
		Assertions.assertEquals("No Account found for ID=1", e.getMessage());

		// (2) Add one account, then check it
		accountHashOperations.put(Constants.CACHE_ACCOUNT_NAME, a1.getId(), a1);
		Account res_2 = accountManager.findById(a1.getId());
		Assertions.assertNotNull(res_2);
		makeAssertionsOnAccounts(a1, res_2);

	}

	@DisplayName("Delete Test: delete an Account by ID")
	@Test
	void delete_Test() throws Exception {

		// (1) ID to be deleted not found
		ItemNotFoundException e = Assertions.assertThrows(ItemNotFoundException.class, () -> accountManager.delete(a1.getId()));
		Assertions.assertEquals("No Account found for ID=1", e.getMessage());

		// (2) Add one Account, delete and then check it
		accountHashOperations.put(Constants.CACHE_ACCOUNT_NAME, id_1, a1);
		Account res_1 = accountManager.delete(a1.getId());
		makeAssertionsOnAccounts(a1, res_1);

	}

	@DisplayName("Update Test: update an Account")
	@Test
	void update_Test() throws Exception {

		// (1) Account ID to be deleted not found
		ItemNotFoundException e = Assertions.assertThrows(ItemNotFoundException.class, () -> accountManager.update(a1));
		Assertions.assertEquals("No Account found for ID=1", e.getMessage());

		// (2) Add one account, then check it
		accountHashOperations.put(Constants.CACHE_ACCOUNT_NAME, id_1, a1);
		Account res_1 = accountManager.update(a1);
		makeAssertionsOnAccounts(a1, res_1);

	}

	@DisplayName("Save Test: save a new Account")
	@Test
	void save_Test() throws Exception {

		// (1) Valid Account: add a new Account
		Account res_1 = accountManager.save(a1);
		Assertions.assertNotNull(res_1);
		makeAssertionsOnAccounts(a1, res_1);
	}
}
