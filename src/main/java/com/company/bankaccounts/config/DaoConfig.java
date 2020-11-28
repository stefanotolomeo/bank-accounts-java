package com.company.bankaccounts.config;

import com.company.bankaccounts.dao.client.AccountRepository;
import com.company.bankaccounts.dao.manager.AccountManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { AccountManager.class, AccountRepository.class, })
public class DaoConfig {
}
