package com.simplesolutions.medicinesmanager.service.medicine.interactions;

import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.dto.interactiondto.MedicationInteractionDTO;
import com.simplesolutions.medicinesmanager.service.medicine.MedicineDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class InteractionsService {
    private final MedicineDao medicineDao;
    private final InteractionDao interactionDao;
    @Value("#{'${service.medications-interactions.not-found-name.message}'}")
    private String INTERACTION_NOT_FOUND_NAME_MSG;
    @Value("#{'${service.medications-interactions.already-exists-name.message}'}")
    private String INTERACTION_ALREADY_EXISTS_NAME_MSG;


    public List<MedicationInteractions> getMedicineInteractions(Integer patientId, Integer medicineId){
        List<MedicationInteractions> medicationInteractions = interactionDao.selectMedicineInteractions(patientId, medicineId);
        if (medicationInteractions == null || medicationInteractions.isEmpty())
            return Collections.emptyList();
        medicationInteractions.sort(Comparator.comparing(MedicationInteractions::getId));
        return medicationInteractions;
    }

    public MedicationInteractions getMedicineInteractionByName(Integer medicineId, String name){
        String capitalizedName = name.substring(0, 1).toUpperCase() +
                name.substring(1).toLowerCase();
        MedicationInteractions medicationInteraction =  interactionDao.selectMedicineInteractionByName(medicineId, capitalizedName);
        if (Objects.isNull(medicationInteraction))
            throw new ResourceNotFoundException(INTERACTION_NOT_FOUND_NAME_MSG.formatted(capitalizedName));
        return medicationInteraction;
    }
    public void deleteMedicationInteractionByName(Integer medicineId, String name){
        String capitalizedName = name.substring(0, 1).toUpperCase() +
                name.substring(1).toLowerCase();
        if (!interactionDao.doesMedicineInteractionExists(medicineId, capitalizedName))
            throw new ResourceNotFoundException(INTERACTION_NOT_FOUND_NAME_MSG.formatted(capitalizedName));
        interactionDao.deleteMedicineInteractionByName(medicineId, capitalizedName);
    }
    public void saveMedicineInteraction(MedicationInteractionDTO request, Medication medication){
        if (interactionDao.doesMedicineInteractionExists(medication.getId(),request.name())) {
            throw new DuplicateResourceException(INTERACTION_ALREADY_EXISTS_NAME_MSG
                    .formatted(request.name()));
        }
        String capitalizedName = request.name().substring(0, 1).toUpperCase() +
                request.name().substring(1).toLowerCase();

        MedicationInteractions interaction = MedicationInteractions.builder()
                .name(capitalizedName.trim())
                .Type(request.type())
                .build();
        if (!medicineDao.doesPatientMedicineExists( medication.getPatient().getEmail() , medication.getBrandName())){
            throw new ResourceNotFoundException(INTERACTION_NOT_FOUND_NAME_MSG.formatted(medication.getBrandName()));
        }
        interaction.setMedicine(medication);
        interactionDao.saveMedicineInteraction(interaction);
    }

}
