package com.pismo.transaction.pismotransaction.entity.convertor;

import com.pismo.transaction.pismotransaction.constants.OperationTypeEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OperationTypeConvertor implements  AttributeConverter<OperationTypeEnum, Long> {

    @Override
    public Long convertToDatabaseColumn(OperationTypeEnum opType) {
        if (opType == null) {
            return null;
        }
        return opType.getId();
    }

    @Override
    public OperationTypeEnum convertToEntityAttribute(Long id) {
        if (id == null) {
            return null;
        }

        return OperationTypeEnum.get(id);
    }
}
