package com.pismo.transaction.pismotransaction.service;

import com.pismo.transaction.pismotransaction.constants.OperationTypeEnum;
import com.pismo.transaction.pismotransaction.dto.TransactionCreationRequest;
import com.pismo.transaction.pismotransaction.entity.Account;
import com.pismo.transaction.pismotransaction.entity.Transaction;
import com.pismo.transaction.pismotransaction.exception.InvalidTransactionException;
import com.pismo.transaction.pismotransaction.exception.NoRecordFoundException;
import com.pismo.transaction.pismotransaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTransactions_RecordsFound_ReturnRecords() {
        List<Transaction> transactions = new ArrayList<>(Arrays.asList(new Transaction[]{
                newTransaction(1l, 5l, new BigDecimal("100.00"), OperationTypeEnum.CREDIT_VOUCHER),
                newTransaction(2l, 5l, new BigDecimal("-100.00"), OperationTypeEnum.PURCHASE_WITH_INSTALLMENT)}));
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> actual = service.getTransactions();

        assertThat(actual).hasSameElementsAs(transactions);

    }

    @Test
    void getTransactions_NoRecordsFound_ReturnEmptyList() {

        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Transaction> actual = service.getTransactions();

        assertThat(actual).isEmpty();

    }

    @Test
    void getTransaction_RecordFound_ReturnsRecord() throws NoRecordFoundException {

        Transaction transaction = newTransaction(1l, 5l, new BigDecimal("100.00"), OperationTypeEnum.CREDIT_VOUCHER);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThat(service.getTransaction(1L)).isEqualTo(transaction);
    }

    @Test
    void getTransaction_NoRecordFound_ThrowsException() {

        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTransaction(1L))
                .isInstanceOf(NoRecordFoundException.class)
                .hasMessageContaining("Transaction with Id 1 not found");
    }

    @Test
    void addTransaction_NoAccountFound_ThrowsException() throws NoRecordFoundException {

        Transaction transaction = newTransaction(1l, 5l, new BigDecimal("100.00"), OperationTypeEnum.CREDIT_VOUCHER);

        TransactionCreationRequest request = new TransactionCreationRequest(transaction.getAccount().getId(),
                transaction.getOperationType(), transaction.getAmount());

        when(accountService.getAccount(5L)).thenThrow(new NoRecordFoundException("Account with Id 5 not found"));

        assertThatThrownBy(() -> service.addTransaction(request))
                .isInstanceOf(NoRecordFoundException.class)
                .hasMessageContaining("Account with Id 5 not found");

    }

    @Test
    void addTransaction_ValidRequest_CreatesTransaction() throws NoRecordFoundException, InvalidTransactionException {


        Transaction transaction = newTransaction(1l, 5l, new BigDecimal("100.00"), OperationTypeEnum.CREDIT_VOUCHER);

        TransactionCreationRequest request = new TransactionCreationRequest(transaction.getAccount().getId(),
                transaction.getOperationType(), transaction.getAmount());

        when(accountService.getAccount(5L)).thenReturn(new Account(5l, "1234"));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);


        Transaction actual = service.addTransaction(request);
        assertThat(actual).isEqualTo(transaction);

    }

    @Test
    void addTransaction_InvalidAmount_ThrowsException() throws NoRecordFoundException {

        when(accountService.getAccount(5L)).thenReturn(new Account(5l, "1234"));

        assertThatThrownBy(() -> service.addTransaction(new TransactionCreationRequest(5L, OperationTypeEnum.CREDIT_VOUCHER, BigDecimal.ZERO)),
                "Validate for zero amount")
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Transaction amount of 0.0 is invalid");


        assertTransactionAmount(new BigDecimal(-100), OperationTypeEnum.CREDIT_VOUCHER, "Transaction amount should be +ve for Operation");

        assertTransactionAmount(new BigDecimal(100), OperationTypeEnum.PURCHASE_WITH_INSTALLMENT, "Transaction amount should be -ve for Operation");

        assertTransactionAmount(new BigDecimal(100), OperationTypeEnum.NORMAL_PURCHASE, "Transaction amount should be -ve for Operation");

        assertTransactionAmount(new BigDecimal(100), OperationTypeEnum.WITHDRAWAL, "Transaction amount should be -ve for Operation");

    }

    private void assertTransactionAmount(BigDecimal amount, OperationTypeEnum typeEnum, String message) {
        assertThatThrownBy(() -> service.addTransaction(new TransactionCreationRequest(5L,
                        typeEnum, amount)),
                "Validate for " + typeEnum.toString())
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining(message + " " + typeEnum);
    }

    private Transaction newTransaction(Long tranId, Long accountID, BigDecimal amount, OperationTypeEnum type) {

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setAccount(new Account(accountID, ""));
        transaction.setId(tranId);
        transaction.setOperationType(type);

        return transaction;
    }
}