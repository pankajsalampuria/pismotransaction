package com.pismo.transaction.pismotransaction.service;

import com.pismo.transaction.pismotransaction.entity.Account;
import com.pismo.transaction.pismotransaction.exception.DuplicateRecordException;
import com.pismo.transaction.pismotransaction.exception.NoRecordFoundException;
import com.pismo.transaction.pismotransaction.repository.AccountRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Adds new account in the repository
     *
     * @param account
     * @throws DuplicateRecordException - If account with same number already exists
     */
    public Account addAccount(Account account) throws DuplicateRecordException {
        Optional<Account> existingAccount = getAccountByNumber(account.getNumber());

        if (existingAccount.isPresent()) {
            throw new DuplicateRecordException("Account " + account.getNumber() + " already exists");
        }
        return accountRepository.save(account);
    }

    /**
     * Saves account details in repository
     *
     * @param account
     * @throws DuplicateRecordException - If account with same number already exists
     * @throws NoRecordFoundException   - If no record found for given id
     */
    public Account updateAccount(Account account, Long id) throws DuplicateRecordException, NoRecordFoundException {
        Optional<Account> existingAccount = accountRepository.findById(id);

        Account dbAccount = existingAccount.orElseThrow(() -> new NoRecordFoundException("Account with Id " + id + " not found"));

        existingAccount = getAccountByNumber(account.getNumber());

        if (existingAccount.isPresent() && existingAccount.get().getId() != id) {
            throw new DuplicateRecordException("Account " + account.getNumber() + " already exists");
        }

        dbAccount.setNumber(account.getNumber());

        return accountRepository.save(dbAccount);
    }

    /**
     * Returns account for given ID.
     *
     * @param id
     * @return
     * @throws NoRecordFoundException - If no record found for this id
     */
    public Account getAccount(Long id) throws NoRecordFoundException {
        Optional<Account> existingAccount = accountRepository.findById(id);

        if (!existingAccount.isPresent()) {
            throw new NoRecordFoundException("Account with Id " + id + " not found");
        }
        return existingAccount.get();
    }

    /**
     * Returns all accounts in the repository
     *
     * @return
     */
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Returns all accounts in the repository sorted by account number
     *
     * @return
     */
    public List<Account> getAccountsSortedByNumber() {
        return accountRepository.findAll(Sort.by(Sort.Direction.ASC, "number"));
    }

    /**
     * Returns account for given number
     *
     * @param number
     * @return
     * @throws DuplicateRecordException - If more than 1 record found for this number
     */
    public Optional<Account> getAccountByNumber(String number) throws DuplicateRecordException {

        List<Account> accounts = accountRepository.findByNumber(number);

        if (!CollectionUtils.isEmpty(accounts)) {
            if (accounts.size() > 1) {
                throw new DuplicateRecordException("Multiple record with Account " + number + " found");
            }
            return Optional.of(accounts.get(0));
        }
        return Optional.empty();
    }

    /**
     * Deletes account from repository for given ID
     *
     * @param id
     * @throws NoRecordFoundException - If no account found
     */
    public void deleteAccount(Long id) throws NoRecordFoundException {
        Account account = getAccount(id);

        accountRepository.deleteById(id);
    }
}
