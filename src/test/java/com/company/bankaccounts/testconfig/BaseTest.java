package com.company.bankaccounts.testconfig;

import com.company.bankaccounts.config.DaoConfig;
import com.company.bankaccounts.config.RedisConfig;
import com.company.bankaccounts.config.WebConfig;
import com.company.bankaccounts.dao.model.AbstractTransaction;
import com.company.bankaccounts.dao.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = { TestContext.class, RedisConfig.class, DaoConfig.class, WebConfig.class })
@ExtendWith(SpringExtension.class)
@JsonTest
// @TestPropertySource(locations = "classpath:application-unit.yml")
@ActiveProfiles("unit")
public abstract class BaseTest extends BDDMockito {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@MockBean
	public RedisTemplate<String, Object> redisTemplate;

	@MockBean
	public HashOperations<String, String, Account> accountHashOperations;

	@MockBean
	public HashOperations<String, String, AbstractTransaction> transactionHashOperations;

	@MockBean
	public ValueOperations<String, Object> valueOperations;

}