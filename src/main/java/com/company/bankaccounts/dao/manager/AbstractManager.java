package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public abstract class AbstractManager {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	protected String CACHE_NAME;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ValueOperations<String, Object> valueOperations;

	String getNextId(String cache) {
		Long newId = null;
		switch (cache) {
		case Constants.CACHE_ACCOUNT_NAME:
			newId = valueOperations.increment(Constants.INDEX_CACHE_ACCOUNT);
			break;
		case Constants.CACHE_TRANSACTION_NAME:
			newId = valueOperations.increment(Constants.INDEX_CACHE_TRANSACTION);
			break;
		default:
			// TODO exception
		}
		log.debug("NextId is {}", newId);
		return String.valueOf(newId);
	}

}
