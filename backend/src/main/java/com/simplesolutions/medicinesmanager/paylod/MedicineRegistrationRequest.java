package com.simplesolutions.medicinesmanager.paylod;

import com.simplesolutions.medicinesmanager.utils.StringListConverter;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineRegistrationRequest {
    @NotBlank(message = "Brand name is required")
    final String brandName;
    final String activeIngredient;
    @NotNull(message = "times medicine taken daily is required")
    final int timesDaily;
    @NotBlank(message = "for safety reasons, instructions are required")
    final String instructions;
    @Convert(converter = StringListConverter.class)
    final List<String> interactions;
}
