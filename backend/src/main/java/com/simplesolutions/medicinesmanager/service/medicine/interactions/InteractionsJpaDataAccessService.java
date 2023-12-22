package com.simplesolutions.medicinesmanager.service.medicine.interactions;

import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.repository.MedicationInteractionRepository;
import com.simplesolutions.medicinesmanager.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class InteractionsJpaDataAccessService implements InteractionDao {
    private final MedicineRepository medicineRepository;
    private final MedicationInteractionRepository interactionRepository;

    @Override
    public MedicationInteractions selectMedicineInteractionByName(Integer medicineId, String name) {
        return interactionRepository.findByMedicineIdAndName(medicineId, name);
    }

    @Override
    public List<MedicationInteractions> selectMedicineInteractions(Integer patientId, Integer medicineId) {
        Medicine medicine = medicineRepository.findByPatientIdAndId(patientId, medicineId)
                .orElseThrow(() -> new ResourceNotFoundException(" Medicine doesn't exist"));
        return medicine.getInteractions();
    }

    @Override
    public void saveMedicineInteraction(MedicationInteractions interaction) {
        interactionRepository.save(interaction);
    }

    @Override
    public void deleteMedicineInteractionByName(Integer medicineId, String name) {
        MedicationInteractions interaction = interactionRepository.findByMedicineIdAndName(medicineId, name);
        interactionRepository.delete(interaction);
    }

    @Override
    public boolean doesMedicineInteractionExists(Integer medicineId, String name) {
        return interactionRepository.existsByMedicineIdAndName(medicineId, name);
    }
}
