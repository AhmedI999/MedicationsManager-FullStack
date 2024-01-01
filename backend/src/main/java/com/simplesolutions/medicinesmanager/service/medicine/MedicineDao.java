package com.simplesolutions.medicinesmanager.service.medicine;

import com.simplesolutions.medicinesmanager.model.Medication;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineDao {

    Optional<Medication> selectPatientMedicineById(Integer patientId, Integer medicineId);

    Optional<Medication> selectPatientMedicineByBrandName(Integer patientId, String brandName);
    List<Medication> selectPatientMedicines(Integer patientId);
    void saveMedicine(Medication medication);
    void updateMedicine(Medication medication);
    void deletePatientMedicineById(Integer patientId, Integer medicineId);

    boolean doesPatientMedicineExists(String email, String brandName);

}
