package com.simplesolutions.medicinesmanager.service.medicine;

import com.simplesolutions.medicinesmanager.model.Medicine;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineDao {

    Optional<Medicine> selectPatientMedicineById(Integer patientId, Integer medicineId);
    List<Medicine> selectPatientMedicines(Integer patientId);
    void saveMedicine(Medicine medicine);
    void updateMedicine(Medicine medicine);
    void deletePatientMedicineById(Integer patientId, Integer medicineId);

    boolean doesPatientMedicineExists(String email, String brandName);

}
