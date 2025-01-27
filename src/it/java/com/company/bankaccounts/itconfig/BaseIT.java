package com.company.bankaccounts.itconfig;

import com.company.bankaccounts.config.DaoConfig;
import com.company.bankaccounts.config.RedisConfig;
import com.company.bankaccounts.dao.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PostConstruct;
import java.util.Set;

@ContextConfiguration(classes = { ITContext.class, RedisConfig.class, DaoConfig.class })
@ExtendWith(SpringExtension.class)
@JsonTest
@ActiveProfiles("it")
public abstract class BaseIT {

	@Value("${cache.account}")
	protected String CACHE_ACCOUNT_NAME;

	@Value("${cache.transaction}")
	protected String CACHE_TRANSACTION_NAME;

	@Autowired
	protected RedisTemplate<String, Object> redisTemplate;

	protected HashOperations<String, String, Account> accountHashOperations;

	protected HashOperations<String, String, AbstractTransaction> transactionHashOperations;

	@PostConstruct
	public void initialize() {
		accountHashOperations = redisTemplate.opsForHash();
		transactionHashOperations = redisTemplate.opsForHash();
	}

	protected void clearAllCaches() {
		// Clear ACCOUNT cache
		Set<String> accountKeys = accountHashOperations.keys(CACHE_ACCOUNT_NAME);
		for (String s : accountKeys) {
			accountHashOperations.delete(CACHE_ACCOUNT_NAME, s);
		}

		// Clear TRANSACTIONS cache
		Set<String> transactionKeys = transactionHashOperations.keys(CACHE_TRANSACTION_NAME);
		for (String s : transactionKeys) {
			transactionHashOperations.delete(CACHE_TRANSACTION_NAME, s);
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
		Assertions.assertEquals(expected.getType(), actual.getType());
		Assertions.assertEquals(expected.getId(), actual.getId());
		Assertions.assertEquals(expected.getAmount(), actual.getAmount());

		switch (actual.getType()) {
		case WITHDRAW:
			Assertions.assertEquals(((TransactionWithdraw) expected).getAccountId(), ((TransactionWithdraw) actual).getAccountId());
			break;
		case DEPOSIT:
			Assertions.assertEquals(((TransactionDeposit) expected).getAccountId(), ((TransactionDeposit) actual).getAccountId());
			break;
		case TRANSFER:
			TransactionTransfer castedExpected = (TransactionTransfer) expected;
			TransactionTransfer castedActual = (TransactionTransfer) actual;
			Assertions.assertEquals(castedExpected.getAccountId(), castedActual.getAccountId());
			Assertions.assertEquals(castedExpected.getToAccountId(), castedActual.getToAccountId());
			break;
		default:
			throw new Exception("Unrecognized TransactionType");
		}
	}

}