package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.dao.client.AccountRepository;
import com.company.bankaccounts.dao.client.IndexRepository;
import com.company.bankaccounts.dao.client.TransactionRepository;
import com.company.bankaccounts.dao.model.*;
import com.company.bankaccounts.exceptions.FailedCRUDException;
import com.company.bankaccounts.exceptions.InsufficientAmountException;
import com.company.bankaccounts.exceptions.InvalidInputException;
import com.company.bankaccounts.exceptions.ItemNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
public class TransactionManager extends AbstractManager implements IManager<AbstractTransaction> {

	@Value("${init.fixture.transaction.enabled}")
	private boolean initWithFixtures;

	@Value("${init.fixture.transaction.path}")
	private String PATH_DEPOSIT;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private IndexRepository indexRepository;

	@PostConstruct
	public void initialize() throws Exception {
		if (initWithFixtures) {
			ObjectMapper mapper = new ObjectMapper();
			List<AbstractTransaction> initTrans = new ArrayList<>();

			// (1) Add Deposit transactions
			try (InputStream is = getClass().getResourceAsStream(PATH_DEPOSIT)) {
				initTrans.addAll(mapper.readValue(is, new TypeReference<List<TransactionDeposit>>() {
				}));
			}

			for (AbstractTransaction t : initTrans) {
				this.save(t);
			}
		}
	}

	// At this layer, the PIN has been checked yet
	@Override
	public AbstractTransaction save(AbstractTransaction item) throws Exception {

		String nextTransactionId = indexRepository.getNextIdForTransaction();
		item.setId(nextTransactionId);

		switch (item.getType()) {
		case WITHDRAW:
			TransactionWithdraw withdrawTrans = (TransactionWithdraw) item;
			saveWithdrawTransaction(withdrawTrans);
			break;
		case DEPOSIT:
			TransactionDeposit depositTrans = (TransactionDeposit) item;
			saveDepositTransaction(depositTrans);
			break;
		case TRANSFER:
			TransactionTransfer trasferTrans = (TransactionTransfer) item;
			saveTransferTransaction(trasferTrans);
			break;
		}

		return findById(nextTransactionId);
	}

	@Override
	public AbstractTransaction update(AbstractTransaction item) throws Exception {
		// ASSUMPTION: Cannot UPDATE a transaction. It will bring the involved accounts in an INCONSISTENT status.
		throw new FailedCRUDException(OperationType.UPDATE, "Unsupported operation: cannot manually update an TRANSACTION record");
	}

	@Override
	public AbstractTransaction findById(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new InvalidInputException("Invalid Input: Null or empty");
		}

		AbstractTransaction foundTrans = transactionRepository.getById(id);
		if (foundTrans == null) {
			throw new ItemNotFoundException("No Transaction found for ID=" + id);
		}

		return foundTrans;
	}

	@Override
	public Map<String, AbstractTransaction> findAll() {
		return transactionRepository.getAll();
	}

	@Override
	public AbstractTransaction delete(String id) throws Exception {
		// ASSUMPTION: Cannot DELETE a transaction. It will bring the involved accounts in an INCONSISTENT status.
		throw new FailedCRUDException(OperationType.DELETE, "Unsupported operation: cannot manually delete a TRANSACTION record");
	}

	public void saveWithdrawTransaction(TransactionWithdraw transactionWithdraw) throws Exception {

		Account acc = accountRepository.getById(transactionWithdraw.getAccountId());

		if (acc == null) {
			String msg = String.format("Cannot save WITHDRAW Transaction: AccountID=%s not found", transactionWithdraw.getAccountId());
			throw new ItemNotFoundException(msg);
		}

		BigDecimal newAmount = acc.getAmount().subtract(transactionWithdraw.getAmount());

		if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new InsufficientAmountException("Not enough amount", acc.getAmount(), transactionWithdraw.getAmount());
		}
		log.info("WITHDRAW Transaction ALLOWED: oldAmount={}, transactionAmount={}, newAmount={}", acc.getAmount(),
				transactionWithdraw.getAmount(), newAmount);
		acc.setAmount(newAmount);
		transactionRepository.save(transactionWithdraw, Collections.singletonList(acc));
	}

	public void saveDepositTransaction(TransactionDeposit transactionDeposit) throws Exception {

		Account acc = accountRepository.getById(transactionDeposit.getAccountId());

		if (acc == null) {
			String msg = String.format("Cannot save DEPOSIT Transaction: AccountID=%s not found", transactionDeposit.getAccountId());
			throw new ItemNotFoundException(msg);
		}

		BigDecimal newAmount = acc.getAmount().add(transactionDeposit.getAmount());

		log.info("DEPOSIT Transaction: oldAmount={}, transactionAmount={}, newAmount={}", acc.getAmount(), transactionDeposit.getAmount(),
				newAmount);

		acc.setAmount(newAmount);
		transactionRepository.save(transactionDeposit, Collections.singletonList(acc));
	}

	public void saveTransferTransaction(TransactionTransfer transactionTransfer) throws Exception {

		if (transactionTransfer.getAccountId().equals(transactionTransfer.getToAccountId())) {
			throw new InvalidInputException("Same account, cannot move funds on the same account");
		}

		Account fromAccount = accountRepository.getById(transactionTransfer.getAccountId());
		Account toAccount = accountRepository.getById(transactionTransfer.getToAccountId());

		if (fromAccount == null || toAccount == null) {
			String msg = String.format("Cannot save TRANSFER Transaction: FromAccountID=%s or ToAccountId=%s not found",
					transactionTransfer.getAccountId(), transactionTransfer.getToAccountId());
			throw new ItemNotFoundException(msg);
		}

		BigDecimal fromAcc_newAmount = fromAccount.getAmount().subtract(transactionTransfer.getAmount());
		BigDecimal toAcc_newAmount = toAccount.getAmount().add(transactionTransfer.getAmount());

		if (fromAcc_newAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new InsufficientAmountException("Not enough amount", fromAccount.getAmount(), transactionTransfer.getAmount());
		}

		log.info("TRANSFER Transaction: Account_FROM has oldAmount={}, transactionAmount={}, newAmount={}; "
						+ "Account_TO has oldAmount={}, transactionAmount={}, newAmount={};", fromAccount.getAmount(),
				transactionTransfer.getAmount(), fromAcc_newAmount, toAccount.getAmount(), transactionTransfer.getAmount(),
				toAcc_newAmount);
		fromAccount.setAmount(fromAcc_newAmount);
		toAccount.setAmount(toAcc_newAmount);
		transactionRepository.save(transactionTransfer, Arrays.asList(fromAccount, toAccount));
	}
}
