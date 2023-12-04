package com.simplesolutions.medicinesmanager.paylod;

import com.simplesolutions.medicinesmanager.utils.StringListConverter;
import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineUpdateRequest {
    final String brandName;
    final String activeIngredient;
    final Integer timesDaily;
    final String instructions;
    @Convert(converter = StringListConverter.class)
    final List<String> interactions;
}
