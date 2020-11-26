package com.company.bankaccounts.controller;

import com.company.bankaccounts.controller.dto.AccountDTO;
import com.company.bankaccounts.controller.dto.BaseAccountDTO;
import com.company.bankaccounts.controller.logic.AccountConverter;
import com.company.bankaccounts.controller.logic.AccountValidator;
import com.company.bankaccounts.dao.manager.AccountManager;
import com.company.bankaccounts.dao.model.Account;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/account")
public class AccountController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private AccountConverter converter;

	@Autowired
	private AccountValidator validator;

	@PostMapping
	public ResponseEntity<String> save(
			@RequestBody
			final AccountDTO account) throws Exception {

		log.debug("Received Save-Account request={}", account);

		log.debug("Validating request..");
		validator.validate(account);

		log.debug("Request is valid. Converting into Internal Model..");
		Account acc = converter.convertToInternalModel(account);

		log.debug("Converted Account is={}. Saving account..", acc);
		Account savedAccount = accountManager.save(acc);

		log.debug("Successfully added Account={}", savedAccount);
		return ResponseEntity.status(HttpStatus.OK).body(savedAccount.getId());
	}

	@GetMapping("/all")
	public ResponseEntity<Map<String, AccountDTO>> findAll() {
		log.debug("Getting all Accounts");

		// @formatter:off
			Map<String, AccountDTO> res = accountManager.findAll()
					.entrySet()
					.stream()
					.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> converter.convertToDTO(entry.getValue())
				));
			// @formatter:on
		log.debug("Found {} accounts. Returning..", res.size());
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountDTO> findById(
			@PathVariable("id")
			final String id) throws Exception {

		log.debug("Getting Account with ID={}", id);
		try {
			Account foundAcc = accountManager.findById(id);

			log.debug("Found account. Converting into DTO..");
			AccountDTO accDTO = converter.convertToDTO(foundAcc);
			log.debug("Converted DTO-Account is={}. Returning..", accDTO);

			return ResponseEntity.status(HttpStatus.OK).body(accDTO);
		} catch (ItemNotFoundException e) {
			// This exception cannot be managed from the general ExceptionHandler because here the status is OK

			log.error("No element found", e);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(
			@PathVariable("id")
			final String id) throws Exception {

		log.debug("Deleting Account with ID={}", id);
		Account deletedAccount = accountManager.delete(id);

		log.debug("Successfully deleted Account with ID = {}", deletedAccount.getId());
		return ResponseEntity.status(HttpStatus.OK).body(id);
	}

	@PutMapping
	public ResponseEntity<String> update(
			@RequestBody
			final BaseAccountDTO account, String id) throws Exception {

		log.debug("Received Update-Account request={} for ID={}", account, id);

		log.debug("Validating request..");
		validator.validate(account, id);

		log.debug("Request is valid. Converting into Internal Model..");
		Account acc = converter.convertToInternalModel(account, id);

		log.debug("Converted Account is={}. Updating account..", acc);

		// Note: The following accounts' amounts could be different
		Account updated = accountManager.update(acc);

		log.debug("Successfully updated Account with ID = {}.", updated.getId());

		return ResponseEntity.status(HttpStatus.OK).body(updated.getId());

	}

	// It manages: InvalidInputException, ItemNotFoundException and in general Exception
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<String> handleException(Exception e) {

		if (e instanceof InvalidInputException) {
			log.error("Bad input for request", e);
			String msg = "Bad input: " + e.getMessage();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
		}

		if (e instanceof ItemNotFoundException) {
			log.error("No element found", e);
			String msg = "No element found: " + e.getMessage();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
		}

		// Default
		log.error("Unexpected error", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

}
