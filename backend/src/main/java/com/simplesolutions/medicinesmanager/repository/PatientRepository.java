package com.simplesolutions.medicinesmanager.repository;

import com.simplesolutions.medicinesmanager.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    boolean existsPatientByEmail(String email);

    Optional<Patient> findByEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE Patient p SET p.enabled = TRUE WHERE p.email = ?1")
    int enablePatientAccount(String email);
}
