package com.pismo.transaction.pismotransaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.transaction.pismotransaction.entity.Account;
import com.pismo.transaction.pismotransaction.exception.DuplicateRecordException;
import com.pismo.transaction.pismotransaction.exception.NoRecordFoundException;
import com.pismo.transaction.pismotransaction.service.AccountService;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getAll_RecordsFound_ReturnsRecords() throws Exception {
        List<Account> accounts = new ArrayList<>(Arrays.asList(new Account[]{
                new Account(1l, "12345"),
                new Account(2l, "12346")}));
        Mockito.when(accountService.getAccounts()).thenReturn(accounts);

        JSONArray expected = new JSONArray();

        for (Account account : accounts) {
            JSONObject object = new JSONObject();
            object.put("id", account.getId());
            object.put("number", account.getNumber());
            expected.put(object);
        }

        ResultActions result = this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/accounts/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        String actual = result.andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("Json Response doesnt match", expected.toString(), actual, JSONCompareMode.LENIENT);

    }

    @Test
    void getAll_NoRecordsFound_ReturnsNoRecords() throws Exception {
        Mockito.when(accountService.getAccounts()).thenReturn(Collections.emptyList());

        JSONArray expected = new JSONArray();

        ResultActions result = this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/accounts/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        String actual = result.andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("Json Response doesnt match", expected.toString(), actual, JSONCompareMode.LENIENT);

    }

    @Test
    void getAccount_AccountFound_ReturnsRecord() throws Exception {

        Account account = new Account(1l, "12345");

        Mockito.when(accountService.getAccount(1l)).thenReturn(account);


        JSONObject expected = new JSONObject();
        expected.put("id", account.getId());
        expected.put("number", account.getNumber());


        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/accounts/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(result1 -> {
                    JSONAssert.assertEquals("Json Response doesnt match", expected.toString(),
                            result1.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
                });


    }

    @Test
    void getAccount_AccountNotFound_Returns404() throws Exception {

        Mockito.when(accountService.getAccount(1l)).thenThrow(new NoRecordFoundException("Account with Id 1 not found"));


        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/accounts/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result1 -> {
                    Assertions.assertThat(result1.getResolvedException())
                            .isInstanceOf(NoRecordFoundException.class)
                            .hasMessageContaining("Account with Id 1 not found");
                });

    }

    @Test
    void addAccount_NewRecord_CreatedAccount() throws Exception {

        Account account = new Account(1l, "12345");

        Mockito.when(accountService.addAccount(Mockito.any(Account.class))).thenReturn(account);

        JSONObject expected = new JSONObject();
        expected.put("id", account.getId());
        expected.put("number", account.getNumber());

        String requestBody = new ObjectMapper().valueToTree(account).toString();

        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/accounts/")
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
    void addAccount_DuplicateAccount_ReturnsDuplicateError() throws Exception {

        Account account = new Account(1l, "12345");

        Mockito.when(accountService.addAccount(Mockito.any(Account.class))).thenThrow(new DuplicateRecordException("Account 12345 already exists"));

        String requestBody = new ObjectMapper().valueToTree(account).toString();

        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/accounts/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> {
                    Assertions.assertThat(result1.getResolvedException())
                            .isInstanceOf(DuplicateRecordException.class)
                            .hasMessageContaining("Account 12345 already exists");
                });
    }




}