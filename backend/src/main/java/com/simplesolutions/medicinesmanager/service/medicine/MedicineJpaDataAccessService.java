package com.simplesolutions.medicinesmanager.service.medicine;

import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.repository.MedicineRepository;
import com.simplesolutions.medicinesmanager.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MedicineJpaDataAccessService implements MedicineDao {
    private final PatientRepository patientRepository;
    private final MedicineRepository medicineRepository;
    @Value("#{'${service.patient.not-found-general.message}'}")
    private String PATIENT_NOT_EXISTS_MSG;
    @Value("#{'${service.patient-medications.not-found-general.message}'}")
    private String MEDICATION_NOT_FOUND_MSG;


    @Override
    public List<Medication> selectPatientMedicines(Integer patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT_NOT_EXISTS_MSG));
        return patient.getPatientMedications();
    }

    @Override
    public Optional<Medication> selectPatientMedicineById(Integer patientId, Integer medicineId) {
        return medicineRepository.findByPatientIdAndId(patientId, medicineId);
    }

    @Override
    public Optional<Medication> selectPatientMedicineByBrandName(Integer patientId, String brandName) {
        return medicineRepository.findByPatientIdAndBrandName(patientId, brandName);
    }

    @Override
    public void saveMedicine(Medication medication) {
        medicineRepository.save(medication);
    }

    @Override
    public void deletePatientMedicineById(Integer patientId, Integer medicineId) {
        Medication patientMedication = medicineRepository.findByPatientIdAndId(patientId, medicineId)
                .orElseThrow(() -> new ResourceNotFoundException(MEDICATION_NOT_FOUND_MSG));
        medicineRepository.delete(patientMedication);
    }
    @Override
    public boolean doesPatientMedicineExists(String email, String brandName) {
        return medicineRepository.existsMedicineByPatient_EmailAndBrandName(email, brandName);
    }
    @Override
    public void updateMedicine(Medication medication) {
        medicineRepository.save(medication);
    }
}
