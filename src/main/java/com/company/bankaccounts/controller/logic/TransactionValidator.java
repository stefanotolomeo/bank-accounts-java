package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.AccountDTO;
import com.company.bankaccounts.controller.dto.DepositTransactionDTO;
import com.company.bankaccounts.controller.dto.TransferTransactionDTO;
import com.company.bankaccounts.controller.dto.WithdrawTransactionDTO;
import com.company.bankaccounts.dao.exceptions.InvalidInputException;
import com.company.bankaccounts.dao.manager.AccountManager;
import com.company.bankaccounts.dao.model.*;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionValidator {

	@Autowired
	private AccountManager accountManager;

	// TODO: if preferred, unify validate() into one method (Create abstraction for input type, then unify)
	public void validate(DepositTransactionDTO transDTO) throws InvalidInputException {

		try {
			Preconditions.checkNotNull(transDTO, "Null Transaction");
			Preconditions.checkNotNull(transDTO.getAmount(), "Null Amount");
			Preconditions.checkArgument(transDTO.getAmount().compareTo(BigDecimal.ZERO) > 0, "Not Positive Amount");
			Preconditions.checkArgument(transDTO.getAccountId() != null && !transDTO.getAccountId().isEmpty(), "Null or Empty AccountID");
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Transaction: " + e.getMessage());
		}
	}

	public void validate(WithdrawTransactionDTO transDTO) throws InvalidInputException {

		try {
			Preconditions.checkNotNull(transDTO, "Null Transaction");
			Preconditions.checkNotNull(transDTO.getAmount(), "Null Amount");
			Preconditions.checkArgument(transDTO.getAmount().compareTo(BigDecimal.ZERO) > 0, "Not Positive Amount");
			Preconditions.checkArgument(transDTO.getAccountId() != null && !transDTO.getAccountId().isEmpty(), "Null or Empty AccountID");
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Transaction: " + e.getMessage());
		}
	}

	public void validate(TransferTransactionDTO transDTO) throws InvalidInputException {

		try {
			Preconditions.checkNotNull(transDTO, "Null Transaction");
			Preconditions.checkNotNull(transDTO.getAmount(), "Null Amount");
			Preconditions.checkArgument(transDTO.getAmount().compareTo(BigDecimal.ZERO) > 0, "Not Positive Amount");
			Preconditions.checkArgument(
					transDTO.getFromAccountId() != null && !transDTO.getFromAccountId().isEmpty(), "Null or Empty From-AccountID");
			Preconditions.checkArgument(
					transDTO.getToAccountId() != null && !transDTO.getToAccountId().isEmpty(), "Null or Empty To-AccountID");
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Transaction: " + e.getMessage());
		}
	}


	public void checkPin(String accountId, String pin) throws Exception {
		Account acc = accountManager.findById(accountId);
		if (!pin.equals(acc.getPin())) {
			throw new InvalidInputException("PIN not valid for AccountID=" + accountId);
		}
	}
}
