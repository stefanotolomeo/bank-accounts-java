package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.AccountDTO;
import com.company.bankaccounts.controller.dto.BaseAccountDTO;
import com.company.bankaccounts.dao.model.Account;
import org.springframework.stereotype.Service;

@Service
public class AccountConverter {

	/**
	 * Null ID here: it is used for saving (ID is automatically assigned)
	 */
	public Account convertToInternalModel(AccountDTO accountDTO) {
		return new Account(null, accountDTO.getName(), accountDTO.getSurname(), accountDTO.getPin(), accountDTO.getAmount());
	}

	/**
	 * Null Amount here: it is used for updating (cannot update amount in this way, only with transactions)
	 */
	public Account convertToInternalModel(BaseAccountDTO accountDTO, String id) {
		return new Account(id, accountDTO.getName(), accountDTO.getSurname(), accountDTO.getPin(), null);
	}

	public AccountDTO convertToDTO(Account account) {
		return new AccountDTO(account.getId(), account.getName(), account.getSurname(), account.getPin(), account.getAmount());
	}
}
