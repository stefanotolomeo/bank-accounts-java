package com.company.bankaccounts.controller;

import com.company.bankaccounts.controller.dto.DepositTransactionDTO;
import com.company.bankaccounts.controller.dto.TransactionDTO;
import com.company.bankaccounts.controller.dto.TransferTransactionDTO;
import com.company.bankaccounts.controller.dto.WithdrawTransactionDTO;
import com.company.bankaccounts.controller.logic.TransactionConverter;
import com.company.bankaccounts.controller.logic.TransactionValidator;
import com.company.bankaccounts.dao.manager.TransactionManager;
import com.company.bankaccounts.dao.model.AbstractTransaction;
import com.company.bankaccounts.dao.model.TransactionTransfer;
import com.company.bankaccounts.dao.model.TransactionType;
import com.company.bankaccounts.dao.model.TransactionWithdraw;
import com.company.bankaccounts.exceptions.InsufficientAmountException;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	public ResponseEntity<Map<String, TransactionDTO>> findAll() {
		log.debug("Getting all Transactions");

		// @formatter:off
			Map<String, TransactionDTO> res = transactionManager.findAll()
					.entrySet()
					.stream()
					.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> converter.convertToDTO(entry.getValue())
				));
			// @formatter:on
		log.debug("Found {} transactions. Returning..", res.size());

		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping("/allByAccount/{id}")
	public ResponseEntity<List<TransactionDTO>> findAllByAccountId(
			@PathVariable("id")
			final String id) {

		log.debug("Getting all Transactions for AccountID={}", id);

		// @formatter:off
			List<TransactionDTO> res = transactionManager.findAll()
					.values()
					.stream()
					.filter(t -> t.getAccountId().equals(id) ||
							(t.getType()==TransactionType.TRANSFER && ((TransactionTransfer)t).getToAccountId().equals(id)))
					.map(t -> converter.convertToDTO(t))
					.sorted(Comparator.comparingInt(o -> Integer.parseInt(o.getId())))
					.collect(Collectors.toList());
			// @formatter:on
		log.debug("Found {} transactions. Returning..", res.size());

		if(res.isEmpty()){
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionDTO> findById(
			@PathVariable("id")
			final String id) throws Exception {
		log.debug("Getting Transaction with ID= " + id);
		try {

			AbstractTransaction foundTrans = transactionManager.findById(id);

			log.debug("Found Transaction={}. Converting into DTO..", foundTrans);
			TransactionDTO transDTO = converter.convertToDTO(foundTrans);
			log.debug("Converted DTO-Account is={}. Returning..", transDTO);

			return ResponseEntity.status(HttpStatus.OK).body(transDTO);
		} catch (ItemNotFoundException e) {
			// This exception cannot be managed from the general ExceptionHandler because here the status is OK

			log.error("No element found", e);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@PostMapping("/withdraw")
	public ResponseEntity<String> saveWithdraw(
			@RequestBody
			final WithdrawTransactionDTO withdraw) throws Exception {

		log.debug("Received Withdraw-Transaction. Validating request={}", withdraw);

		validator.validate(withdraw);

		log.debug("Request is valid. Checking PIN..");
		validator.checkPin(withdraw.getAccountId(), withdraw.getPin());
		log.debug("Pin is valid. Converting into Internal Model..");

		log.debug("Request is valid. Converting into Internal Model..");
		TransactionWithdraw transWithdraw = converter.convertToInternalModel(withdraw);

		log.debug("Converted Withdraw-Transaction is={}. Executing..", transWithdraw);

		AbstractTransaction savedTrans = transactionManager.save(transWithdraw);
		log.debug("Withdraw-Transaction executed successfully: {}", savedTrans);

		return ResponseEntity.status(HttpStatus.OK).body(savedTrans.getId());
	}

	@PostMapping("/deposit")
	public ResponseEntity<String> saveDeposit(
			@RequestBody
			final DepositTransactionDTO deposit) throws Exception {

		log.debug("Received Deposit-Transaction. Validating request={}", deposit);

		validator.validate(deposit);

		log.debug("Request is valid. Checking PIN..");
		validator.checkPin(deposit.getAccountId(), deposit.getPin());
		log.debug("Pin is valid. Executing Transaction..");

		AbstractTransaction savedTrans = transactionManager.save(converter.convertToInternalModel(deposit));
		log.debug("Deposit-Transaction executed successfully: {}", savedTrans);

		return ResponseEntity.status(HttpStatus.OK).body(savedTrans.getId());
	}

	@PostMapping("/transfer")
	public ResponseEntity<String> saveTransfer(
			@RequestBody
			final TransferTransactionDTO transfer) throws Exception {

		log.debug("Received Transfer-Transaction. Validating request={}", transfer);

		validator.validate(transfer);

		log.debug("Request is valid. Checking PIN..");
		validator.checkPin(transfer.getFromAccountId(), transfer.getPin());
		log.debug("Pin is valid. Executing Transaction..");

		AbstractTransaction savedTrans = transactionManager.save(converter.convertToInternalModel(transfer));
		log.debug("Deposit-Transaction executed successfully: {}", savedTrans);

		return ResponseEntity.status(HttpStatus.OK).body(savedTrans.getId());
	}

	// It manages: InvalidInputException, ItemNotFoundException and in general Exception
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<String> handleException(Exception e) {

		if (e instanceof InvalidInputException) {
			log.error("Bad input for request", e);
			String msg = "Bad input: " + e.getMessage();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
		}

		if (e instanceof HttpMessageNotReadableException) {
			log.error("Bad input for request", e);
			String msg = "Bad input: Invalid JSON";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
		}

		if (e instanceof ItemNotFoundException) {
			log.error("No element found", e);
			String msg = "No element found: " + e.getMessage();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
		}

		if (e instanceof InsufficientAmountException) {
			InsufficientAmountException customExp = (InsufficientAmountException) e;
			log.error("Operation not allowed due to insufficient amount", e);
			String msg = String
					.format("Operation not allowed: %s.  Available:%s, Requested=%s", e.getMessage(), customExp.getAvailableFunds(),
							customExp.getRequestedFunds());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
		}

		// Default
		log.error("Unexpected error", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
