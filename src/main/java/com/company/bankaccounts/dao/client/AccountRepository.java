package com.company.bankaccounts.dao.client;

import com.company.bankaccounts.config.Constants;
import com.company.bankaccounts.dao.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Repository
public class AccountRepository {

	private String DATA_CACHE_NAME;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private HashOperations<String, String, Account> hashOperations;

	@PostConstruct
	public void initialize() {
		this.DATA_CACHE_NAME = Constants.CACHE_ACCOUNT_NAME;
		hashOperations = redisTemplate.opsForHash();
	}

	public void save(Account a) {
		hashOperations.put(DATA_CACHE_NAME, a.getId(), a);
	}

	public void update(Account a) {
		hashOperations.put(DATA_CACHE_NAME, a.getId(), a);
	}

	public void delete(String id) {
		hashOperations.delete(DATA_CACHE_NAME, id);
	}

	public Account getById(String id) {
		return hashOperations.get(DATA_CACHE_NAME, id);
	}

	public Map<String, Account> getAll() {
		return hashOperations.entries(DATA_CACHE_NAME);
	}
}
