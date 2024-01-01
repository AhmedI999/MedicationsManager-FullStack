package com.simplesolutions.medicinesmanager.repository;

import com.simplesolutions.medicinesmanager.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    boolean existsPatientByEmail(String email);

    Optional<Patient> findByEmail(String email);

}
