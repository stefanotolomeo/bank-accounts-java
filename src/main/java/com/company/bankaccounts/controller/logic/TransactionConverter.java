package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.DepositTransactionDTO;
import com.company.bankaccounts.controller.dto.TransferTransactionDTO;
import com.company.bankaccounts.controller.dto.WithdrawTransactionDTO;
import com.company.bankaccounts.dao.model.TransactionDeposit;
import com.company.bankaccounts.dao.model.TransactionTransfer;
import com.company.bankaccounts.dao.model.TransactionWithdraw;

public class TransactionConverter {

	public TransactionWithdraw convertToInternalModel(WithdrawTransactionDTO transDTO) {
		return new TransactionWithdraw(null, transDTO.getAmount(), transDTO.getAccountId());
	}

	public TransactionDeposit convertToInternalModel(DepositTransactionDTO transDTO) {
		return new TransactionDeposit(null, transDTO.getAmount(), transDTO.getAccountId());
	}

	public TransactionTransfer convertToInternalModel(TransferTransactionDTO transDTO) {
		return new TransactionTransfer(null, transDTO.getAmount(), transDTO.getFromAccountId(), transDTO.getToAccountId());
	}

	public WithdrawTransactionDTO convertToDTO(TransactionWithdraw withdraw) {
		// TODO
		return null;
	}

	public DepositTransactionDTO convertToDTO(TransactionDeposit deposit) {
		// TODO
		return null;
	}

	public TransferTransactionDTO convertToDTO(TransactionTransfer transfer) {
		// TODO
		return null;
	}
}
