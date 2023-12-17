package com.simplesolutions.medicinesmanager.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (Objects.isNull(attribute) || attribute.equals(List.of("")))
            return "No Interaction Added";
        return String.join(DELIMITER, attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return Arrays.asList(dbData.split(DELIMITER));
    }
}
