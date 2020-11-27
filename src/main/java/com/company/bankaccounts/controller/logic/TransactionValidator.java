package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.DepositTransactionDTO;
import com.company.bankaccounts.controller.dto.TransferTransactionDTO;
import com.company.bankaccounts.controller.dto.WithdrawTransactionDTO;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.dao.manager.AccountManager;
import com.company.bankaccounts.dao.model.Account;
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
			commonValidation(transDTO.getAmount(), transDTO.getPin(), transDTO.getAccountId());
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Transaction: " + e.getMessage());
		}
	}

	public void validate(WithdrawTransactionDTO transDTO) throws InvalidInputException {

		try {
			Preconditions.checkNotNull(transDTO, "Null Transaction");
			commonValidation(transDTO.getAmount(), transDTO.getPin(), transDTO.getAccountId());
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Transaction: " + e.getMessage());
		}
	}

	public void validate(TransferTransactionDTO transDTO) throws InvalidInputException {

		try {
			Preconditions.checkNotNull(transDTO, "Null Transaction");

			commonValidation(transDTO.getAmount(), transDTO.getPin(), transDTO.getFromAccountId());
			Preconditions
					.checkArgument(transDTO.getToAccountId() != null && !transDTO.getToAccountId().isEmpty(), "Null or Empty To-AccountID");
			Preconditions.checkArgument(!transDTO.getFromAccountId().equals(transDTO.getToAccountId()),
					"Source and Destination account cannot be the same");
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Transaction: " + e.getMessage());
		}
	}

	private void commonValidation(BigDecimal amount, String pin, String account){
		Preconditions.checkNotNull(amount, "Null Amount");
		Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "Not Positive Amount");
		Preconditions.checkArgument(account != null && account.trim().length() != 0, "Null or Empty AccountID");
		Preconditions.checkArgument(pin != null && pin.trim().length() != 0, "Null or Empty Pin");
		// here no check on Pin size
	}

	public void checkPin(String accountId, String pin) throws Exception {
		Account acc = accountManager.findById(accountId);
		if (!pin.equals(acc.getPin())) {
			throw new InvalidInputException("PIN not valid for AccountID=" + accountId);
		}
	}
}
