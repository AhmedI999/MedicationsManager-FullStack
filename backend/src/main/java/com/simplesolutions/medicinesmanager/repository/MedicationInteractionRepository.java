package com.simplesolutions.medicinesmanager.repository;

import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface MedicationInteractionRepository extends JpaRepository<MedicationInteractions, Integer> {
    MedicationInteractions findByMedicineIdAndName(Integer medicineId, String name);

    boolean existsByMedicineIdAndName(Integer medicineId, String name);


}
