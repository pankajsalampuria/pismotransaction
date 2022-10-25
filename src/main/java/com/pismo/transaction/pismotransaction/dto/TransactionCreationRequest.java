package com.pismo.transaction.pismotransaction.dto;

import com.pismo.transaction.pismotransaction.constants.OperationTypeEnum;

import java.math.BigDecimal;

public class TransactionCreationRequest {

    private Long accountId;

    private OperationTypeEnum operationType;

    private BigDecimal amount;

    public TransactionCreationRequest() {
        super();
    }

    public TransactionCreationRequest(Long accountId, OperationTypeEnum operationType, BigDecimal amount) {
        this.accountId = accountId;
        this.operationType = operationType;
        this.amount = amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public OperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
