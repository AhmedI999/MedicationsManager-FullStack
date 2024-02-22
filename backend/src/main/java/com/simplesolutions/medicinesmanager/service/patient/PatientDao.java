package com.simplesolutions.medicinesmanager.service.patient;

import com.simplesolutions.medicinesmanager.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientDao {
    List<Patient> selectAllPatients();

    Optional<Patient> selectPatientById(Integer id);

    Optional<Patient> selectPatientByEmail(String email);
    int enablePatientAccount(String email);

    void updatePatient(Patient patient);

    void savePatient(Patient patient);

    boolean doesPatientExists(String email);

    void deletePatientById(Integer id);
}
