package com.simplesolutions.medicinesmanager.service.patient;

import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Repository
public class PatientJpaDataAccessService implements PatientDao {
    private final PatientRepository patientRepository;
    @Override
    public List<Patient> selectAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> selectPatientById(Integer id) {
        return patientRepository.findById(id);
    }

    @Override
    public void savePatient(Patient patient) {
        patientRepository.save(patient);
    }

    @Override
    public boolean doesPatientExists(String email) {
        return patientRepository.existsPatientByEmail(email);
    }

    @Override
    public void deletePatientById(Integer id) {
        patientRepository.deleteById(id);
    }

    @Override
    public void updatePatient(Patient patient) {
        patientRepository.save(patient);
    }
}
