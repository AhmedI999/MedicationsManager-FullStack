package com.simplesolutions.medicinesmanager.service.medicine;

import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.repository.MedicineRepository;
import com.simplesolutions.medicinesmanager.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MedicineJpaDataAccessService implements MedicineDao {
    private final PatientRepository patientRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public List<Medicine> selectPatientMedicines(Integer patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(" Patient doesn't exist"));
        return patient.getPatientMedicines();
    }

    @Override
    public Optional<Medicine> selectPatientMedicineById(Integer patientId, Integer medicineId) {
        return medicineRepository.findByPatientIdAndId(patientId, medicineId);
    }

    @Override
    public void saveMedicine(Medicine medicine) {
        medicineRepository.save(medicine);
    }

    @Override
    public void deletePatientMedicineById(Integer patientId, Integer medicineId) {
        Medicine patientMedicine = medicineRepository.findByPatientIdAndId(patientId, medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Couldn't find medicine"));
        medicineRepository.delete(patientMedicine);
    }
    @Override
    public boolean doesPatientMedicineExists(String email, String brandName) {
        return medicineRepository.existsMedicineByPatient_EmailAndBrandName(email, brandName);
    }
    @Override
    public void updateMedicine(Medicine medicine) {
        medicineRepository.save(medicine);
    }
}
