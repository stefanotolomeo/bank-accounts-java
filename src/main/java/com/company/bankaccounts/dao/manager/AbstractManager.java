package com.company.bankaccounts.dao.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;

public abstract class AbstractManager {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	protected String CACHE_NAME;

	@Autowired
	protected ValueOperations<String, Object> valueOperations;

}
