package com.pismo.transaction.pismotransaction.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OperationTypeEnum {

    NORMAL_PURCHASE(1l),
    PURCHASE_WITH_INSTALLMENT(2L),
    WITHDRAWAL(3L),
    CREDIT_VOUCHER(4L);


    private Long id;

    private OperationTypeEnum(Long id) {
        this.id = id;
    }

    @JsonValue
    public Long getId() {
        return id;
    }

    public static OperationTypeEnum get(Long id) {
        return Arrays.stream(OperationTypeEnum.values())
                .filter((e) -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid operation Type : " + id));
    }

}
