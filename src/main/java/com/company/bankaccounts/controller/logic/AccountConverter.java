package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.AccountDTO;
import com.company.bankaccounts.dao.model.Account;
import org.springframework.stereotype.Service;

@Service
public class AccountConverter {

	public Account convertToInternalModel(AccountDTO accountDTO) {
		return new Account(null, accountDTO.getName(), accountDTO.getSurname(), accountDTO.getPin(), accountDTO.getAmount());
	}

	public Account convertToInternalModel(AccountDTO accountDTO, String id) {
		return new Account(id, accountDTO.getName(), accountDTO.getSurname(), accountDTO.getPin(), accountDTO.getAmount());
	}

	public AccountDTO convertToDTO(Account account) {
		return new AccountDTO(account.getId(), account.getName(), account.getSurname(), account.getPin(), account.getAmount());
	}
}
