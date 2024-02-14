package com.simplesolutions.medicinesmanager.dto.medicationsdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public record MedicineRegistrationRequest(
        @URL(message = "Invalid URL")
        @Pattern(regexp = ".*\\.(jpeg|jpg|gif|png)$", message = "Invalid image URL")
        String pictureUrl,
        @NotBlank(message = "Brand name is required")
        String brandName,
        String activeIngredient,
        @NotNull(message = "times medication taken daily is required")
        @Min(value = 1, message = "Must be greater than 0")
        int timesDaily,
        @NotBlank(message = "for safety reasons, instructions are required")
        String instructions) {}
