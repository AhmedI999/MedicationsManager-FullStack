package com.simplesolutions.medicinesmanager.paylod;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicineResponse {
    final Integer medicineNumber;
    final String pictureUrl;
    final String brandName;
    final String activeIngredient;
    final int timesDaily;
    final String instructions;
    final List<MedicationInteractions> interactions;
}
