package com.company.bankaccounts.config;

import com.company.bankaccounts.controller.AccountController;
import com.company.bankaccounts.controller.logic.TransactionValidator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { AccountController.class, TransactionValidator.class })
public class WebConfig {

}
