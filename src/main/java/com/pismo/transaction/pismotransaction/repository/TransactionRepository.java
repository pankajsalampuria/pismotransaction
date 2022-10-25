package com.pismo.transaction.pismotransaction.repository;

import com.pismo.transaction.pismotransaction.entity.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public List<Transaction> findByAccountId(Long accountId);

    @Query (value = "SELECT T FROM Transaction T WHERE T.account.id = ?1")
    public List<Transaction> findByAccountId(Long accountId, Sort sort)  ;

    @Query (value = "SELECT T FROM Transaction T WHERE T.account.id = ?1 AND T.operationType = ?2")
    public List<Transaction> findByAccountIdAndOperationTypeId(Long accountId, Long operationId);

}
