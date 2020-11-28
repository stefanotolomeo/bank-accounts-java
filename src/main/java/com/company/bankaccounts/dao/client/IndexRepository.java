package com.company.bankaccounts.dao.client;

import com.company.bankaccounts.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class IndexRepository {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private ValueOperations<String, Object> valueOperations;

	@PostConstruct
	public void initialize() {
		valueOperations = redisTemplate.opsForValue();
	}

	public String getNextIdForAccount() {
		return String.valueOf(valueOperations.increment(Constants.INDEX_CACHE_ACCOUNT));
	}

	public String getNextIdForTransaction() {
		return String.valueOf(valueOperations.increment(Constants.INDEX_CACHE_TRANSACTION));
	}

}
