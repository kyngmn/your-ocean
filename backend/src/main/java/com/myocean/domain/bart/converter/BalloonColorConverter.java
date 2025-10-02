package com.myocean.domain.bart.converter;

import com.myocean.domain.bart.enums.BalloonColor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BalloonColorConverter implements AttributeConverter<BalloonColor, String> {

    @Override
    public String convertToDatabaseColumn(BalloonColor attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public BalloonColor convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return BalloonColor.valueOf(dbData);
    }
}