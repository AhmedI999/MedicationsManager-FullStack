package com.simplesolutions.medicinesmanager.service.medicine.interactions;

import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionDao {
    MedicationInteractions selectMedicineInteractionByName(Integer medicineId, String name);
    List<MedicationInteractions> selectMedicineInteractions(Integer patientId, Integer medicineId);

    void saveMedicineInteraction(MedicationInteractions interaction);

    void deleteMedicineInteractionByName(Integer medicineId, String name);
    boolean doesMedicineInteractionExists(Integer medicineId, String name);
}
