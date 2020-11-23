package com.company.bankaccounts.itconfig;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.config.RedisConfig;
import com.company.bankaccounts.dao.model.AbstractTransaction;
import com.company.bankaccounts.dao.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

@ContextConfiguration(classes = { ITContext.class, RedisConfig.class })
@ExtendWith(SpringExtension.class)
@JsonTest
// @TestPropertySource(locations = "classpath:application-unit.yml")
@ActiveProfiles("it")
public abstract class BaseIT extends BDDMockito {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Autowired
	protected HashOperations<String, String, Account> accountHashOperations;

	@Autowired
	protected HashOperations<String, String, AbstractTransaction> transactionHashOperations;

	protected void clearAllCaches() {
		// Clear ACCOUNT cache
		Set<String> accountKeys = accountHashOperations.keys(Constants.CACHE_ACCOUNT_NAME);
		for (String s : accountKeys) {
			accountHashOperations.delete(Constants.CACHE_ACCOUNT_NAME, s);
		}

		// Clear TRANSACTIONS cache
		Set<String> transactionKeys = transactionHashOperations.keys(Constants.CACHE_TRANSACTION_NAME);
		for (String s : transactionKeys) {
			transactionHashOperations.delete(Constants.CACHE_TRANSACTION_NAME, s);
		}
	}

	protected void makeAssertionsOnAccounts(Account expected, Account actual) {
		Assertions.assertEquals(expected.getId(), actual.getId());
		Assertions.assertEquals(expected.getName(), actual.getName());
		Assertions.assertEquals(expected.getSurname(), actual.getSurname());
		Assertions.assertEquals(expected.getPin(), actual.getPin());
		Assertions.assertEquals(expected.getAmount(), actual.getAmount());
	}

	protected void makeAssertionsOnTransactions(AbstractTransaction expected, AbstractTransaction actual) throws Exception {
		// Cannot know ID and Timestamp: no assertions for them
		Assertions.assertEquals(expected.getAmount(), actual.getAmount());
		Assertions.assertEquals(expected.getTransactionType(), actual.getTransactionType());

		// TODO
		switch (actual.getTransactionType()) {
		case TRANSFER:
			// TransactionTransfer exp_1 = (Tra)
			break;
		case DEPOSIT:
			break;
		case WITHDRAW:
			break;
		default:
			throw new Exception("Unrecognized TransactionType");
		}
	}

}