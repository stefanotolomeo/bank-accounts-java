package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.AccountDTO;
import com.company.bankaccounts.controller.dto.BaseAccountDTO;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountValidator {

	private final String PIN_PATTERN = "[0-9]+";	// RegEx for PIN
	/**
	 * Used for POST-Request validation
	 */
	public void validate(AccountDTO accountDTO) throws InvalidInputException {

		try {
			Preconditions.checkNotNull(accountDTO, "Null Account");
			// Make common validations
			commonValidation(accountDTO.getName(), accountDTO.getSurname(), accountDTO.getPin());
			// Validate Amount
			Preconditions.checkNotNull(accountDTO.getAmount(), "Null Amount");
			Preconditions.checkArgument(accountDTO.getAmount().compareTo(BigDecimal.ZERO) > 0, "Not Positive Amount");
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Account: " + e.getMessage());
		}
	}

	/**
	 * Used for PUT-Request validation
	 */
	public void validate(BaseAccountDTO accountDTO, String id) throws InvalidInputException {

		try {
			// For PUT-Request: it is needed to check the ID
			Preconditions.checkNotNull(accountDTO, "Null Account");
			Preconditions.checkArgument(id != null && id.trim().length() != 0, "Null or Empty ID");
			commonValidation(accountDTO.getName(), accountDTO.getSurname(), accountDTO.getPin());
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Account: " + e.getMessage());
		}
	}

	void commonValidation(String name, String surname, String pin){
		Preconditions.checkArgument(name != null && name.trim().length() != 0, "Null or Empty Name");
		Preconditions.checkArgument(surname != null && surname.trim().length() != 0, "Null or Empty Surname");
		Preconditions.checkArgument(pin != null && pin.trim().length() != 0, "Null or Empty Pin");
		Preconditions.checkArgument(pin.matches(PIN_PATTERN), "Invalid Pin: only digits allowed");
		Preconditions.checkArgument(pin.trim().length() == 4, "Invalid Pin: only 4 digits allowed");
	}
}
