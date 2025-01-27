package com.company.bankaccounts.testconfig;

import com.company.bankaccounts.controller.AccountController;
import com.company.bankaccounts.controller.TransactionController;
import com.company.bankaccounts.dao.manager.AccountManager;
import com.company.bankaccounts.dao.manager.TransactionManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({})
public class TestContext {

	@MockBean
	public AccountController accountController;

	@MockBean
	public TransactionController transactionController;

	@MockBean
	public AccountManager accountManager;

	@MockBean
	public TransactionManager transactionManager;

}
