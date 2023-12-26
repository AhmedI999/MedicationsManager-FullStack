package com.simplesolutions.medicinesmanager.service.medicine.interactions;

import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.dto.MedicationInteractionRequest;
import com.simplesolutions.medicinesmanager.service.medicine.MedicineDao;
import lombok.RequiredArgsConstructor;
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
            throw new ResourceNotFoundException("This interaction %s doesn't exist".formatted(capitalizedName));
        return medicationInteraction;
    }
    public void deleteMedicationInteractionByName(Integer medicineId, String name){
        String capitalizedName = name.substring(0, 1).toUpperCase() +
                name.substring(1).toLowerCase();
        if (!interactionDao.doesMedicineInteractionExists(medicineId, capitalizedName))
            throw new ResourceNotFoundException("Medicine Interaction %s wasn't found".formatted(capitalizedName));
        interactionDao.deleteMedicineInteractionByName(medicineId, capitalizedName);
    }
    public void saveMedicineInteraction(MedicationInteractionRequest request, Medicine medicine){
        if (interactionDao.doesMedicineInteractionExists(medicine.getId(),request.getName())) {
            throw new DuplicateResourceException("Medicine's Interaction (%s) already Exists"
                    .formatted(request.getName()));
        }
        String capitalizedName = request.getName().substring(0, 1).toUpperCase() +
                request.getName().substring(1).toLowerCase();

        MedicationInteractions interaction = MedicationInteractions.builder()
                .name(capitalizedName)
                .Type(request.getType())
                .build();
        if (!medicineDao.doesPatientMedicineExists( medicine.getPatient().getEmail() ,medicine.getBrandName())){
            throw new ResourceNotFoundException("Medicine %s doesn't exist".formatted(medicine.getBrandName()));
        }
        interaction.setMedicine(medicine);
        interactionDao.saveMedicineInteraction(interaction);
    }

}
