package com.company.bankaccounts.config;

import com.company.bankaccounts.dao.manager.AccountManager;
import com.company.bankaccounts.dao.model.AbstractTransaction;
import com.company.bankaccounts.dao.model.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ComponentScan(basePackageClasses = { AccountManager.class })
public class RedisConfig {

	// Setting up the Jedis connection factory.
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(10);
		poolConfig.setMaxIdle(5);
		poolConfig.setMinIdle(1);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setMaxWaitMillis(10 * 1000);

		return new JedisConnectionFactory(poolConfig);

	}

	// Setting up the Redis templates object.
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
		redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}

	@Bean
	public HashOperations<String, String, Account> accountHashOperations() {
		return redisTemplate().opsForHash();
	}

	@Bean
	public HashOperations<String, String, AbstractTransaction> transactionHashOperations() {
		return redisTemplate().opsForHash();
	}


	@Bean
	public ValueOperations<String, Object> valueOperations() {
		// (1) Initiliaze ValueOperations Caches (for Index)
		ValueOperations<String, Object> valueOperations = redisTemplate().opsForValue();
		valueOperations.setIfAbsent(Constants.INDEX_CACHE_TRANSACTION, 0L);
		valueOperations.setIfAbsent(Constants.INDEX_CACHE_ACCOUNT, 0L);
		return valueOperations;
	}

}