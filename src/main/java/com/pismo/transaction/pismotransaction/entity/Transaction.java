package com.pismo.transaction.pismotransaction.entity;

import com.pismo.transaction.pismotransaction.constants.OperationTypeEnum;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "TRANSACTIONS",
        indexes = {@Index(name = "is_tran_accountId", columnList = "account_id, operationType_id"),
                @Index(name = "is_tran_opTypeId", columnList = "operationType_id")
        })
public class Transaction extends AuditableEntity {

    @Id
    @GeneratedValue
    @ApiModelProperty(accessMode = ApiModelProperty.AccessMode.READ_ONLY, readOnly = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    @ApiModelProperty(dataType = "Long")
    private Account account;

    @Column(name = "operationType_id")
    private OperationTypeEnum operationType;

    @NotNull(message = "Transaction amount must be supplied")
    private BigDecimal amount;


    @ApiModelProperty(accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private OffsetDateTime eventDate;


    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {
        return id;
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

    public OffsetDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(OffsetDateTime eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id.equals(that.id) && account.equals(that.account) && operationType == that.operationType && amount.equals(that.amount) && eventDate.equals(that.eventDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, operationType, amount, eventDate);
    }
}
