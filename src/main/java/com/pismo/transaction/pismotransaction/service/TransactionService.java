package com.pismo.transaction.pismotransaction.service;

import com.pismo.transaction.pismotransaction.constants.OperationTypeEnum;
import com.pismo.transaction.pismotransaction.dto.TransactionCreationRequest;
import com.pismo.transaction.pismotransaction.entity.Account;
import com.pismo.transaction.pismotransaction.entity.Transaction;
import com.pismo.transaction.pismotransaction.exception.InvalidTransactionException;
import com.pismo.transaction.pismotransaction.exception.NoRecordFoundException;
import com.pismo.transaction.pismotransaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    public TransactionService(TransactionRepository repository) {
        this.transactionRepository = repository;
    }

    /**
     * Returns all transactions in the repository
     *
     * @return
     */
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }


    /**
     * Returns transaction for given Id
     *
     * @param id
     * @return
     * @throws NoRecordFoundException
     */
    public Transaction getTransaction(Long id) throws NoRecordFoundException {

        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (!transaction.isPresent()) {
            throw new NoRecordFoundException("Transaction with Id " + id + " not found");
        }
        return transaction.get();
    }

    /**
     * Returns all transactions for given account Id
     *
     * @param accountId
     * @return
     */
    public List<Transaction> getTransactionsForAccount(Long accountId) {

        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        return transactions;
    }


    /**
     * Adds new transaction in the repository
     *
     * @param transaction
     * @throws NoRecordFoundException      - If account for the transaction doesnt exists
     * @throws InvalidTransactionException - Transaction amount is invalid.
     */
    public Transaction addTransaction(TransactionCreationRequest transaction) throws NoRecordFoundException, InvalidTransactionException {

        Account account = accountService.getAccount(transaction.getAccountId());

        validateAmount(transaction);

        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(account);
        newTransaction.setOperationType(transaction.getOperationType());
        newTransaction.setAmount(transaction.getAmount());
        newTransaction.setEventDate(OffsetDateTime.now());

        newTransaction.setBalance(newTransaction.getAmount());

        if(transaction.getOperationType() == OperationTypeEnum.CREDIT_VOUCHER) {
            newTransaction.setBalance(adjustDebitTransactionBalances(transaction));
        }

        return transactionRepository.save(newTransaction);
    }

    private BigDecimal adjustDebitTransactionBalances(TransactionCreationRequest transaction) {

        if(transaction.getOperationType() != OperationTypeEnum.CREDIT_VOUCHER) {
            throw new IllegalArgumentException("Payment discharge is only applicable for Credit Vouchers");
        }

        List<Long> operationTypes = new ArrayList<>();
        operationTypes.add(OperationTypeEnum.NORMAL_PURCHASE.getId());
        operationTypes.add(OperationTypeEnum.WITHDRAWAL.getId());
        operationTypes.add(OperationTypeEnum.PURCHASE_WITH_INSTALLMENT.getId());

        List<Transaction> debitTransactions = transactionRepository.findByAccountIdAndOperationTypeIdNonBalancedRecords(transaction.getAccountId(), operationTypes);

        return adjustDebitTransactionBalances(debitTransactions, transaction.getAmount());
    }

    BigDecimal adjustDebitTransactionBalances(List<Transaction> debitTransactions, BigDecimal creditAmount) {

        BigDecimal remainingBalance =  creditAmount;

        for(Transaction trans : debitTransactions) {

            remainingBalance = adjustTransactionBalance(remainingBalance, trans);
            transactionRepository.save(trans);

            if(remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        return remainingBalance;
    }

    private BigDecimal adjustTransactionBalance(BigDecimal remainingBalance, Transaction trans) {
        BigDecimal balanceAmount= trans.getBalance();

        if(trans.getBalance().negate().compareTo(remainingBalance) > 0) {
            trans.setBalance(balanceAmount.add(remainingBalance));
        } else {
            trans.setBalance(BigDecimal.ZERO);
        }
        remainingBalance = remainingBalance.add(balanceAmount);

        if(remainingBalance.compareTo(BigDecimal.ZERO) < 0) {
            remainingBalance = BigDecimal.ZERO;
        }

        return remainingBalance;
    }


    /**
     * Checks if transaction amount is != 0
     * For Credit voucher amount should be > 0 and for other it should be < 0
     *
     * @param transaction
     * @throws InvalidTransactionException - If amount is invalid
     */
    private void validateAmount(TransactionCreationRequest transaction) throws InvalidTransactionException {

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidTransactionException("Transaction amount of 0.0 is invalid");
        }

        switch (transaction.getOperationType()) {
            case WITHDRAWAL:
            case PURCHASE_WITH_INSTALLMENT:
            case NORMAL_PURCHASE:
                if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0)
                    throw new InvalidTransactionException("Transaction amount should be -ve for Operation " + transaction.getOperationType().toString());
                break;
            case CREDIT_VOUCHER:
                if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0)
                    throw new InvalidTransactionException("Transaction amount should be +ve for Operation " + transaction.getOperationType().toString());
                break;
        }
    }


}
