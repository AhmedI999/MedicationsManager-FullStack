package com.simplesolutions.medicinesmanager.repository;

import com.simplesolutions.medicinesmanager.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface MedicineRepository extends JpaRepository<Medication, Integer> {
    boolean existsMedicineByPatient_EmailAndBrandName(String email, String brandName);
    Optional<Medication> findByPatientIdAndId(Integer patientId, Integer medicineId);
    Optional<Medication> findByPatientIdAndBrandName(Integer id, String brandName);
}
