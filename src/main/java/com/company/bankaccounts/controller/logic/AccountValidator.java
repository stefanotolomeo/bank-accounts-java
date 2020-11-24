package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.AccountDTO;
import com.company.bankaccounts.dao.exceptions.InvalidInputException;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountValidator {

	// Used fof POST
	public void validate(AccountDTO accountDTO) throws InvalidInputException {

		try {
			// For POST-Request: no need to check the ID (it will be assigned automatically)
			commonValidation(accountDTO);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Account: " + e.getMessage());
		}
	}

	// Used fof PUT
	public void validate(AccountDTO accountDTO, String id) throws InvalidInputException {

		try {
			// For PUT-Request: it is needed to check the ID
			Preconditions.checkArgument(id != null && !id.isEmpty(), "Null or Empty ID");
			commonValidation(accountDTO);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Account: " + e.getMessage());
		}
	}

	private void commonValidation(AccountDTO accDTO){
		Preconditions.checkNotNull(accDTO, "Null Account");
		Preconditions.checkArgument(accDTO.getName() != null && !accDTO.getName().isEmpty(), "Null or Empty Name");
		Preconditions.checkArgument(accDTO.getSurname() != null && !accDTO.getSurname().isEmpty(), "Null or Empty Surname");
		Preconditions.checkArgument(accDTO.getPin() != null && !accDTO.getPin().isEmpty(), "Null or Empty Pin");
		Preconditions.checkArgument(accDTO.getPin().length() == 4, "Invalid Pin: only 4 digits allowed");
		Preconditions.checkNotNull(accDTO.getAmount(), "Null Amount");
		Preconditions.checkArgument(accDTO.getAmount().compareTo(BigDecimal.ZERO) > 0, "Not Positive Amount");
	}
}
