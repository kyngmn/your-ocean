package com.myocean.domain.diarychat.converter;

import com.myocean.domain.diarychat.enums.AiStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AiStatusConverter implements AttributeConverter<AiStatus, String> {

    @Override
    public String convertToDatabaseColumn(AiStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public AiStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return AiStatus.valueOf(dbData);
    }
}