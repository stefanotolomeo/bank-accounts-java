package com.company.bankaccounts.dao.client;

import com.company.bankaccounts.dao.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Repository
public class AccountRepository {

	@Value("${cache.account}")
	private String CACHE_NAME;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private HashOperations<String, String, Account> hashOperations;

	@PostConstruct
	public void initialize() {
		hashOperations = redisTemplate.opsForHash();
	}

	public void save(Account a) {
		hashOperations.put(CACHE_NAME, a.getId(), a);
	}

	public void update(Account a) {
		hashOperations.put(CACHE_NAME, a.getId(), a);
	}

	public void delete(String id) {
		hashOperations.delete(CACHE_NAME, id);
	}

	public Account getById(String id) {
		return hashOperations.get(CACHE_NAME, id);
	}

	public Map<String, Account> getAll() {
		return hashOperations.entries(CACHE_NAME);
	}
}
