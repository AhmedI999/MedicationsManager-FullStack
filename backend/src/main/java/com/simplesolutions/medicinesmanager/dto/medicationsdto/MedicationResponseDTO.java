package com.simplesolutions.medicinesmanager.dto.medicationsdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.simplesolutions.medicinesmanager.dto.interactiondto.MedicationInteractionDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MedicationResponseDTO(
        Integer id,
        Integer medicineNumber,
        String pictureUrl,
        String brandName,
        String activeIngredient,
        int timesDaily,
        String instructions,
        List<MedicationInteractionDTO> interactions) {}
