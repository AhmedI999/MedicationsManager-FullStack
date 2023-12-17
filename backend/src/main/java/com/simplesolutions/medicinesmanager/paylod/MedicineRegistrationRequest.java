package com.simplesolutions.medicinesmanager.paylod;

import com.simplesolutions.medicinesmanager.utils.StringListConverter;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineRegistrationRequest {
    @URL(message = "Invalid URL")
    @Pattern(regexp = ".*\\.(jpeg|jpg|gif|png)$", message = "Invalid image URL")
    final String pictureUrl;
    @NotBlank(message = "Brand name is required")
    final String brandName;
    final String activeIngredient;
    @NotNull(message = "times medicine taken daily is required")
    @Min(value = 1, message = "Must be greater than 0")
    final int timesDaily;
    @NotBlank(message = "for safety reasons, instructions are required")
    final String instructions;
    @Convert(converter = StringListConverter.class)
    final List<String> interactions;
}
