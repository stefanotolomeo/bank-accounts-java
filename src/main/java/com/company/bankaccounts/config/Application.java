package com.company.bankaccounts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

import java.text.NumberFormat;

@Configuration
@SpringBootApplication(scanBasePackageClasses = Application.class)
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		printJavaOpts();
		new SpringApplicationBuilder(Application.class).web(WebApplicationType.SERVLET).run(args);
	}

	private static void printJavaOpts() {

		Runtime runtime = Runtime.getRuntime();

		final NumberFormat format = NumberFormat.getInstance();
		final long maxMemory = runtime.maxMemory();
		final long allocatedMemory = runtime.totalMemory();
		final long freeMemory = runtime.freeMemory();

		final long mbSize = 1024L * 1024L;
		final String mega = " MB";

		log.info("============================ Bank-Accounts =============================");
		log.info("============================ Memory Info =============================");
		log.info("Free memory: {}", format.format(freeMemory / mbSize) + mega);
		log.info("Allocated memory: {}", format.format(allocatedMemory / mbSize) + mega);
		log.info("Max memory: {}", format.format(maxMemory / mbSize) + mega);
		log.info("Total free memory: {}", format.format((freeMemory + maxMemory - allocatedMemory) / mbSize) + mega);
		log.info("=================================================================\n");
	}
}