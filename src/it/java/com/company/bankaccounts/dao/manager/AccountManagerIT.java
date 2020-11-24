package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.exceptions.InvalidInputException;
import com.company.bankaccounts.dao.exceptions.ItemNotFoundException;
import com.company.bankaccounts.dao.model.AbstractTransaction;
import com.company.bankaccounts.dao.model.Account;
import com.company.bankaccounts.dao.model.OperationType;
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
		Account res_1 = accountManager.findById(a1.getId());
		Assertions.assertNull(res_1);

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
		Assertions.assertEquals("Cannot Delete: Account ID not found", e.getMessage());

		// (2) Add one Account, delete and then check it
		accountHashOperations.put(Constants.CACHE_ACCOUNT_NAME, id_1, a1);
		Account res_1 = accountManager.delete(a1.getId());
		makeAssertionsOnAccounts(a1, res_1);

	}

	@DisplayName("Update Test: update an Account")
	@Test
	void update_Test() throws Exception {

		// (1) Invalid Account: null
		InvalidInputException e1 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.update(null));
		Assertions.assertEquals("Invalid Account: Null Account", e1.getMessage());

		// (2) Invalid Account: null ID
		Account invalidAccount = new Account(null, null, null, null, null);
		InvalidInputException e2 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.update(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null ID", e2.getMessage());

		// (3) Invalid Account: null name
		invalidAccount.setId("1111");
		InvalidInputException e3 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.update(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null Name", e3.getMessage());

		// (3) Invalid Account: null surname
		invalidAccount.setName("NAME");
		InvalidInputException e4 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.update(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null Surname", e4.getMessage());

		// (4) Invalid Account: null pin
		invalidAccount.setSurname("SURNAME");
		InvalidInputException e5 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.update(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null or Empty Pin", e5.getMessage());

		// (5) Invalid Account: empty pin
		invalidAccount.setPin("");
		InvalidInputException e6 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.update(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null or Empty Pin", e6.getMessage());

		// (6) Valid Account: no cares about amount (it is not considered for updates)
		invalidAccount.setPin("12345");

		// (7) Account ID to be deleted not found
		ItemNotFoundException e7 = Assertions.assertThrows(ItemNotFoundException.class, () -> accountManager.update(a1));
		Assertions.assertEquals("Cannot Update: Account ID not found", e7.getMessage());

		// (8) Add one account, then check it
		accountHashOperations.put(Constants.CACHE_ACCOUNT_NAME, id_1, a1);
		Account res_1 = accountManager.update(a1);
		makeAssertionsOnAccounts(a1, res_1);

	}

	@DisplayName("Save Test: save a new Account")
	@Test
	void save_Test() throws Exception {

		// (1) Invalid Account: null
		InvalidInputException e1 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.save(null));
		Assertions.assertEquals("Invalid Account: Null Account", e1.getMessage());

		// (2) Invalid Account: null name
		Account invalidAccount = new Account(null, null, null, null, null);
		InvalidInputException e2 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.save(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null Name", e2.getMessage());

		// (3) Invalid Account: null surname
		invalidAccount.setName("NAME");
		InvalidInputException e3 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.save(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null Surname", e3.getMessage());

		// (4) Invalid Account: null pin
		invalidAccount.setSurname("SURNAME");
		InvalidInputException e4 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.save(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null or Empty Pin", e4.getMessage());

		// (5) Invalid Account: empty pin
		invalidAccount.setPin("");
		InvalidInputException e5 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.save(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null or Empty Pin", e5.getMessage());

		// (6) Invalid Account: null amount
		invalidAccount.setPin("12345");
		InvalidInputException e6 = Assertions.assertThrows(InvalidInputException.class, () -> accountManager.save(invalidAccount));
		Assertions.assertEquals("Invalid Account: Null Amount", e6.getMessage());

		// (7) Valid Account: add a new Account
		Account res_1 = accountManager.save(a1);
		Assertions.assertNotNull(res_1);
		makeAssertionsOnAccounts(a1, res_1);
	}

	@DisplayName("Account Validation Test")
	@Test
	void validateAccount_Test() throws InvalidInputException {

		// (1) Invalid Account for INSERT: null
		validateAndAssertException(OperationType.INSERT, null, "Invalid Account: Null Account");

		// (2) Invalid Account for INSERT: null name
		Account invalidAccount = new Account(null, null, null, null, null);
		validateAndAssertException(OperationType.INSERT, invalidAccount, "Invalid Account: Null Name");

		// (3) Invalid Account for INSERT: null surname
		invalidAccount.setName("NAME");
		validateAndAssertException(OperationType.INSERT, invalidAccount, "Invalid Account: Null Surname");

		// (4) Invalid Account for INSERT: null pin
		invalidAccount.setSurname("SURNAME");
		validateAndAssertException(OperationType.INSERT, invalidAccount, "Invalid Account: Null or Empty Pin");

		// (5) Invalid Account for INSERT: empty pin
		invalidAccount.setPin("");
		validateAndAssertException(OperationType.INSERT, invalidAccount, "Invalid Account: Null or Empty Pin");

		// (6) Invalid Account for INSERT: null amount
		invalidAccount.setPin("12345");
		validateAndAssertException(OperationType.INSERT, invalidAccount, "Invalid Account: Null Amount");

		// (7) Valid Account for INSERT: no exception here
		invalidAccount.setAmount(BigDecimal.TEN);
		accountManager.validateAccount(OperationType.INSERT, invalidAccount);

		// (8) Invalid Account for UPDATE: null ID
		validateAndAssertException(OperationType.UPDATE, invalidAccount, "Invalid Account: Null ID");

		// (9) Valid Account for UPDATE: no exception here
		invalidAccount.setId("888");
		accountManager.validateAccount(OperationType.UPDATE, invalidAccount);
	}

	private void validateAndAssertException(OperationType opType, Account acc, String msg){
		InvalidInputException e = Assertions
				.assertThrows(InvalidInputException.class, () -> accountManager.validateAccount(opType, acc));
		Assertions.assertEquals(msg, e.getMessage());
	}
}
