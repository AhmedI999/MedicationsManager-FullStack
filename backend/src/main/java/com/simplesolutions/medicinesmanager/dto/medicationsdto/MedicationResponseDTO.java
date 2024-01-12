package com.simplesolutions.medicinesmanager.dto.medicationsdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.simplesolutions.medicinesmanager.dto.interactiondto.MedicationInteractionDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicationResponseDTO {
    final Integer id;
    final Integer medicineNumber;
    final String pictureUrl;
    final String brandName;
    final String activeIngredient;
    final int timesDaily;
    final String instructions;
    final List<MedicationInteractionDTO> interactions;
}
