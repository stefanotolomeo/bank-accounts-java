package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.AccountDTO;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountValidator {

	/**
	 * Used for POST-Request validation
	 */
	public void validate(AccountDTO accountDTO) throws InvalidInputException {

		try {
			// For POST-Request: no need to check the ID (it will be assigned automatically)
			commonValidation(accountDTO);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Account: " + e.getMessage());
		}
	}

	/**
	 * Used for PUT-Request validation
	 */
	public void validate(AccountDTO accountDTO, String id) throws InvalidInputException {

		try {
			// For PUT-Request: it is needed to check the ID
			Preconditions.checkArgument(id != null && id.trim().length() != 0, "Null or Empty ID");
			commonValidation(accountDTO);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Account: " + e.getMessage());
		}
	}

	void commonValidation(AccountDTO accDTO){
		Preconditions.checkNotNull(accDTO, "Null Account");
		Preconditions.checkArgument(accDTO.getName() != null && accDTO.getName().trim().length() != 0, "Null or Empty Name");
		Preconditions.checkArgument(accDTO.getSurname() != null && accDTO.getSurname().trim().length() != 0, "Null or Empty Surname");
		Preconditions.checkArgument(accDTO.getPin() != null && accDTO.getPin().trim().length() != 0, "Null or Empty Pin");
		Preconditions.checkArgument(accDTO.getPin().matches("[0-9]+"), "Invalid Pin: only digits allowed");
		Preconditions.checkArgument(accDTO.getPin().trim().length() == 4, "Invalid Pin: only 4 digits allowed");
		Preconditions.checkNotNull(accDTO.getAmount(), "Null Amount");
		Preconditions.checkArgument(accDTO.getAmount().compareTo(BigDecimal.ZERO) > 0, "Not Positive Amount");
	}
}
