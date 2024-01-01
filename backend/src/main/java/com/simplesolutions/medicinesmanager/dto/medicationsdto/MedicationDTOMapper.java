package com.simplesolutions.medicinesmanager.dto.medicationsdto;

import com.simplesolutions.medicinesmanager.dto.interactiondto.MedicationInteractionDTO;
import com.simplesolutions.medicinesmanager.model.Medication;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MedicationDTOMapper implements Function<Medication, MedicationResponseDTO> {

    @Override
    public MedicationResponseDTO apply(Medication medication) {
        return new MedicationResponseDTO (
                medication.getMedicineNumber(),
                medication.getPictureUrl(),
                medication.getBrandName(),
                medication.getActiveIngredient(),
                medication.getTimesDaily(),
                medication.getInstructions(),
                medication.getInteractions().stream()
                        .map(interaction -> new MedicationInteractionDTO(
                                interaction.getName(),
                                interaction.getType()
                        ))
                        .sorted(Comparator.comparing(MedicationInteractionDTO::getType))
                        .collect(Collectors.toList())
        );
    }
}
