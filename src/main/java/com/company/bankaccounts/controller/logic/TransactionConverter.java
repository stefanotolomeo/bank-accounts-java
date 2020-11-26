package com.company.bankaccounts.controller.logic;

import com.company.bankaccounts.controller.dto.DepositTransactionDTO;
import com.company.bankaccounts.controller.dto.TransactionDTO;
import com.company.bankaccounts.controller.dto.TransferTransactionDTO;
import com.company.bankaccounts.controller.dto.WithdrawTransactionDTO;
import com.company.bankaccounts.dao.model.AbstractTransaction;
import com.company.bankaccounts.dao.model.TransactionDeposit;
import com.company.bankaccounts.dao.model.TransactionTransfer;
import com.company.bankaccounts.dao.model.TransactionWithdraw;
import org.springframework.stereotype.Service;

@Service
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

	public TransactionDTO convertToDTO(AbstractTransaction t) {

		switch (t.getType()) {
		case WITHDRAW:
			TransactionWithdraw withdraw = (TransactionWithdraw) t;
			return new WithdrawTransactionDTO(withdraw.getId(), withdraw.getAmount(), withdraw.getAccountId());
		case DEPOSIT:
			TransactionDeposit deposit = (TransactionDeposit) t;
			return new DepositTransactionDTO(deposit.getId(), deposit.getAmount(), deposit.getAccountId());
		case TRANSFER:
			TransactionTransfer transfer = (TransactionTransfer) t;
			return new TransferTransactionDTO(transfer.getId(), transfer.getAmount(), transfer.getAccountId(),
					transfer.getToAccountId());
		default:
			throw new RuntimeException("Unrecognized TransactionType");
		}
	}
}
