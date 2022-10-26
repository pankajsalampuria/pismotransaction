package com.pismo.transaction.pismotransaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.transaction.pismotransaction.constants.OperationTypeEnum;
import com.pismo.transaction.pismotransaction.dto.TransactionCreationRequest;
import com.pismo.transaction.pismotransaction.entity.Account;
import com.pismo.transaction.pismotransaction.entity.Transaction;
import com.pismo.transaction.pismotransaction.exception.InvalidTransactionException;
import com.pismo.transaction.pismotransaction.exception.NoRecordFoundException;
import com.pismo.transaction.pismotransaction.service.AccountService;
import com.pismo.transaction.pismotransaction.service.TransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @Test
    void getAll_RecordsFound_ReturnsRecords() throws Exception {

        List<Transaction> transactions = new ArrayList<>(Arrays.asList(new Transaction[]{
                newTransaction(1l, 5l, new BigDecimal("100.00"), OperationTypeEnum.CREDIT_VOUCHER),
                newTransaction(2l, 5l, new BigDecimal("-100.00"), OperationTypeEnum.PURCHASE_WITH_INSTALLMENT)}));
        Mockito.when(transactionService.getTransactions()).thenReturn(transactions);

        String expected = new ObjectMapper().valueToTree(transactions).toString();

        ResultActions result = this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/transactions/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(result1 -> {
                    JSONAssert.assertEquals("Json Response doesnt match", expected,
                            result1.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

                });
    }

    @Test
    void getAll_NoRecordsFound_ReturnsNoRecords() throws Exception {


        Mockito.when(transactionService.getTransactions()).thenReturn(Collections.emptyList());

        String expected = new ObjectMapper().valueToTree(Collections.emptyList()).toString();

        ResultActions result = this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/transactions/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(result1 -> {
                    JSONAssert.assertEquals("Json Response doesnt match", expected,
                            result1.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

                });
    }

    @Test
    void getTransaction_TransactionFound_ReturnsRecord() throws Exception {

        Transaction transaction =
                newTransaction(1l, 5l, new BigDecimal("100.00"), OperationTypeEnum.CREDIT_VOUCHER);

        Mockito.when(transactionService.getTransaction(1L)).thenReturn(transaction);

        String expected = new ObjectMapper().valueToTree(transaction).toString();

        ResultActions result = this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/transactions/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(result1 -> {
                    JSONAssert.assertEquals("Json Response doesnt match", expected,
                            result1.getResponse().getContentAsString(), JSONCompareMode.LENIENT);

                });
    }

    @Test
    void getTransaction_TransactionNotFound_Returns404() throws Exception {

        Mockito.when(transactionService.getTransaction(1L)).thenThrow(new NoRecordFoundException("Transaction with Id 1 not found"));

        ResultActions result = this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/transactions/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result1 -> {
                    Assertions.assertThat(result1.getResolvedException())
                            .isInstanceOf(NoRecordFoundException.class)
                            .hasMessageContaining("Transaction with Id 1 not found");
                });
    }

    @Test
    void addTransaction_validRecord_ReturnsSuccess() throws Exception {

        Transaction transaction =
                newTransaction(1l, 5l, new BigDecimal("100.00"), OperationTypeEnum.CREDIT_VOUCHER);

        TransactionCreationRequest request = new TransactionCreationRequest(transaction.getAccount().getId(),
                transaction.getOperationType(), transaction.getAmount());

        Mockito.when(transactionService.addTransaction(Mockito.any(TransactionCreationRequest.class))).thenReturn(transaction);

        String expected = new ObjectMapper().valueToTree(transaction).toString();

        String requestBody = new ObjectMapper().valueToTree(request).toString();


        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/transactions/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(result1 -> {
                    JSONAssert.assertEquals("Json Response doesnt match", expected.toString(),
                            result1.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
                });
    }

    @Test
    void addTransaction_NoAccountRecord_ReturnsFailure() throws Exception {

        Transaction transaction =
                newTransaction(1l, 5l, new BigDecimal("100.00"), OperationTypeEnum.CREDIT_VOUCHER);

        TransactionCreationRequest request = new TransactionCreationRequest(transaction.getAccount().getId(),
                transaction.getOperationType(), transaction.getAmount());

        Mockito.when(transactionService.addTransaction(Mockito.any(TransactionCreationRequest.class)))
                .thenThrow(new NoRecordFoundException("Account with Id 5 not found"));

        String requestBody = new ObjectMapper().valueToTree(request).toString();


        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/transactions/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result1 -> {
                    Assertions.assertThat(result1.getResolvedException())
                            .isInstanceOf(NoRecordFoundException.class)
                            .hasMessageContaining("Account with Id 5 not found");
                });
    }

    @Test
    void addTransaction_InvalidAmount_ReturnsFailure() throws Exception {

        Transaction transaction =
                newTransaction(1l, 5l, new BigDecimal("-100.00"), OperationTypeEnum.CREDIT_VOUCHER);

        TransactionCreationRequest request = new TransactionCreationRequest(transaction.getAccount().getId(),
                transaction.getOperationType(), transaction.getAmount());

        Mockito.when(transactionService.addTransaction(Mockito.any(TransactionCreationRequest.class)))
                .thenThrow(new InvalidTransactionException("Transaction amount should be +ve for Operation CREDIT_VOUCHER"));

        String requestBody = new ObjectMapper().valueToTree(request).toString();


        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/transactions/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> {
                    Assertions.assertThat(result1.getResolvedException())
                            .isInstanceOf(InvalidTransactionException.class)
                            .hasMessageContaining("Transaction amount should be +ve for Operation CREDIT_VOUCHER");
                });
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