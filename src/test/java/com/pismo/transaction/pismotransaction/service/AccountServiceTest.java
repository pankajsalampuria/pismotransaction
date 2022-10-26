package com.pismo.transaction.pismotransaction.service;

import com.pismo.transaction.pismotransaction.entity.Account;
import com.pismo.transaction.pismotransaction.exception.DuplicateRecordException;
import com.pismo.transaction.pismotransaction.exception.NoRecordFoundException;
import com.pismo.transaction.pismotransaction.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService service;

    @Test
    void addAccount_NonExistingAccount_RecordAdded() throws DuplicateRecordException {
        Account account = new Account(1l, "1234");

        Mockito.doReturn(Collections.emptyList()).when(accountRepository).findByNumber("1234");
        Mockito.doReturn(account).when(accountRepository).save(account);

        Account actual = service.addAccount(account);

        assertThat(actual).isEqualTo(account);
    }

    @Test
    void addAccount_ExistingAccount_ThrowsDuplicateException()  {

        Account account = new Account(1l, "1234");

        List<Account> accounts = new ArrayList<>(Arrays.asList(new Account[]{account}));

        Mockito.doReturn(accounts).when(accountRepository).findByNumber("1234");

        assertThatThrownBy(() -> service.addAccount(account), "Duplicate Record Found test")
                .isInstanceOf(DuplicateRecordException.class)
                .hasMessageContaining("Account 1234 already exists");
    }

    @Test
    void getAccount_RecordFound_ReturnsRecord() throws NoRecordFoundException {

        Account account = new Account(1l, "1234");

        Mockito.doReturn(Optional.of(account)).when(accountRepository).findById(1L);

        Account actual = service.getAccount(1L);

        assertThat(actual).isEqualTo(account);
    }

    @Test
    void getAccount_RecordNotFound_ThrowsException() throws NoRecordFoundException {

        Mockito.doReturn(Optional.empty()).when(accountRepository).findById(1L);


        assertThatThrownBy(() -> service.getAccount(1L), "NoRecord Found test")
                .isInstanceOf(NoRecordFoundException.class)
                .hasMessageContaining("Account with Id 1 not found");

    }

    @Test
    void getAccounts_RecordFound_ReturnsRecord() {
        List<Account> accounts = new ArrayList<>(Arrays.asList(new Account[]{
                new Account(1l, "12345"),
                new Account(2l, "12346")}));

        Mockito.doReturn(accounts).when(accountRepository).findAll();

        List<Account> actual = service.getAccounts();

        assertThat(actual).hasSameElementsAs(accounts);
    }

    @Test
    void getAccounts_NoRecordFound_ReturnsEmptyList() {

        Mockito.doReturn(Collections.emptyList()).when(accountRepository).findAll();

        List<Account> actual = service.getAccounts();

        assertThat(actual).isEmpty();
    }
}