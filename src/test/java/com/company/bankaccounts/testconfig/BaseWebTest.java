package com.company.bankaccounts.testconfig;

import com.company.bankaccounts.config.DaoConfig;
import com.company.bankaccounts.config.WebConfig;
import com.company.bankaccounts.dao.model.AbstractTransaction;
import com.company.bankaccounts.dao.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = { TestContext.class, WebConfig.class, DaoConfig.class })
@ExtendWith(SpringExtension.class)
@WebMvcTest
// @TestPropertySource(locations = "classpath:application-unit.yml")
@ActiveProfiles("unit")
public abstract class BaseWebTest {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@MockBean
	public RedisTemplate<String, Object> redisTemplate;

	@MockBean
	public HashOperations<String, String, Account> accountHashOperations;

	@MockBean
	public HashOperations<String, String, AbstractTransaction> transactionHashOperations;

	@MockBean
	public ValueOperations<String, Object> valueOperations;

	protected String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
