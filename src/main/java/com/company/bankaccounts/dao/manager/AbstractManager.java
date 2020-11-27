package com.company.bankaccounts.dao.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;

abstract class AbstractManager {

	final Logger log = LoggerFactory.getLogger(this.getClass());

	String DATA_CACHE_NAME;
	String INDEX_CACHE_NAME;

	@Autowired
	ValueOperations<String, Object> valueOperations;

}
