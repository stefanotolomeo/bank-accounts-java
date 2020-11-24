package com.company.bankaccounts.controller;

import com.company.bankaccounts.controller.dto.DepositTransactionDTO;
import com.company.bankaccounts.controller.dto.TransferTransactionDTO;
import com.company.bankaccounts.controller.dto.WithdrawTransactionDTO;
import com.company.bankaccounts.controller.logic.TransactionConverter;
import com.company.bankaccounts.controller.logic.TransactionValidator;
import com.company.bankaccounts.dao.exceptions.InvalidInputException;
import com.company.bankaccounts.dao.exceptions.ItemNotFoundException;
import com.company.bankaccounts.dao.manager.TransactionManager;
import com.company.bankaccounts.dao.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/transaction")
public class TransactionController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private TransactionValidator validator;

	@Autowired
	private TransactionConverter converter;

	@GetMapping("/all")
	public ResponseEntity<Map<String, AbstractTransaction>> findAll() {
		log.debug("Getting all Transactions");
		try {
			return ResponseEntity.status(HttpStatus.OK).body(transactionManager.findAll());
		} catch (Exception e) {
			log.error("Unexpected error", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<AbstractTransaction> findById(
			@PathVariable("id")
			final String id) {
		log.debug("Getting Transaction with ID= " + id);
		try {
			return ResponseEntity.status(HttpStatus.OK).body(transactionManager.findById(id));
		} catch (InvalidInputException e) {
			log.error("Bad input for request", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (ItemNotFoundException e) {
			log.error("No element found", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception e) {
			log.error("Unexpected error", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}


	@PostMapping("/withdraw")
	public ResponseEntity<String> saveWithdraw(
			@RequestBody
			final WithdrawTransactionDTO withdrawDTO) {

		log.debug("Received Withdraw-Transaction request={}", withdrawDTO);

		try {
			log.debug("Validating request..");
			validator.validate(withdrawDTO);

			log.debug("Request is valid. Checking PIN..");
			validator.checkPin(withdrawDTO.getAccountId(), withdrawDTO.getPin());
			log.debug("Pin is valid. Converting into Internal Model..");

			log.debug("Request is valid. Converting into Internal Model..");
			TransactionWithdraw transWithdraw = converter.convertToInternalModel(withdrawDTO);

			log.debug("Converted Withdraw-Transaction is={}. Executing..", transWithdraw);

			AbstractTransaction savedTrans = transactionManager.save(transWithdraw);
			log.debug("Withdraw-Transaction executed successfully: {}", savedTrans);

			return ResponseEntity.status(HttpStatus.OK).body(savedTrans.getId());
		} catch (InvalidInputException e) {
			log.error("Bad input for request", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			log.error("Unexpected error", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/deposit")
	public ResponseEntity<String> saveDeposit(
			@RequestBody
			final DepositTransactionDTO depositDTO) {

		log.debug("Received Deposit-Transaction request={}", depositDTO);

		try {
			log.debug("Validating request..");
			validator.validate(depositDTO);

			log.debug("Request is valid. Checking PIN..");
			validator.checkPin(depositDTO.getAccountId(), depositDTO.getPin());
			log.debug("Pin is valid. Executing Transaction..");

			AbstractTransaction savedTrans = transactionManager.save(converter.convertToInternalModel(depositDTO));
			log.debug("Deposit-Transaction executed successfully: {}", savedTrans);

			return ResponseEntity.status(HttpStatus.OK).body(savedTrans.getId());
		} catch (InvalidInputException e) {
			log.error("Bad input for request", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			log.error("Unexpected error", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/transfer")
	public ResponseEntity<String> saveTransfer(
			@RequestBody
			final TransferTransactionDTO transferDTO) {

		log.debug("Received Transfer-Transaction request={}", transferDTO);

		try {
			log.debug("Validating request..");
			validator.validate(transferDTO);

			log.debug("Request is valid. Checking PIN..");
			validator.checkPin(transferDTO.getFromAccountId(), transferDTO.getPin());
			log.debug("Pin is valid. Executing Transaction..");

			AbstractTransaction savedTrans = transactionManager.save(converter.convertToInternalModel(transferDTO));
			log.debug("Deposit-Transaction executed successfully: {}", savedTrans);

			return ResponseEntity.status(HttpStatus.OK).body(savedTrans.getId());
		} catch (InvalidInputException e) {
			log.error("Bad input for request", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			log.error("Unexpected error", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
