package com.myocean.domain.ug.converter;

import com.myocean.domain.ug.enums.MoneySize;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MoneySizeConverter implements AttributeConverter<MoneySize, String> {

    @Override
    public String convertToDatabaseColumn(MoneySize attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public MoneySize convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return MoneySize.valueOf(dbData);
    }
}