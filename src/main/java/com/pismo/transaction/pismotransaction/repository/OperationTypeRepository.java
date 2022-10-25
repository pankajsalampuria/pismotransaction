package com.pismo.transaction.pismotransaction.repository;

import com.pismo.transaction.pismotransaction.entity.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {
}
