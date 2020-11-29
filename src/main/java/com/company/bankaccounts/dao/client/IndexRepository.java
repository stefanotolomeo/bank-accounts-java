package com.company.bankaccounts.dao.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class IndexRepository {

	@Value("${cache.index.account}")
	private String CACHE_INDEX_ACCOUNT;

	@Value("${cache.index.transaction}")
	private String CACHE_INDEX_TRANSACTION;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private ValueOperations<String, Object> valueOperations;

	@PostConstruct
	public void initialize() {
		valueOperations = redisTemplate.opsForValue();
	}

	public String getNextIdForAccount() {
		return String.valueOf(valueOperations.increment(CACHE_INDEX_ACCOUNT));
	}

	public String getNextIdForTransaction() {
		return String.valueOf(valueOperations.increment(CACHE_INDEX_TRANSACTION));
	}

}
