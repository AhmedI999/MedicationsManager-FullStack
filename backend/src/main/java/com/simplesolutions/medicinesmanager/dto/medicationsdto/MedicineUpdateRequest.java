package com.simplesolutions.medicinesmanager.dto.medicationsdto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineUpdateRequest {
    @URL(message = "Invalid URL")
    @Pattern(regexp = ".*\\.(jpeg|jpg|gif|png)$", message = "Invalid image URL")
    final String pictureUrl;
    @Pattern(regexp = "^.*\\S.*$", message = "brandName must not be empty")
    final String brandName;
    final String activeIngredient;
    @Min(value = 1, message = "Must be greater than 0")
    final Integer timesDaily;
    @Pattern(regexp = "^.*\\S.*$", message = "instructions must not be empty")
    final String instructions;
}
