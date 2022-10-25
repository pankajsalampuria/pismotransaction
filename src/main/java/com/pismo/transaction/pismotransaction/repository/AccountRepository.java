package com.pismo.transaction.pismotransaction.repository;


import com.pismo.transaction.pismotransaction.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    public List<Account> findByNumber(String number);
}
