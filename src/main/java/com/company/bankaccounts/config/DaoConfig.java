package com.company.bankaccounts.config;

import com.company.bankaccounts.dao.manager.AccountManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { AccountManager.class })
public class DaoConfig {
}
