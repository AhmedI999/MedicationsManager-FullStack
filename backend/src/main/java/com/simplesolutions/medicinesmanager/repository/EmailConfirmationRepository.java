package com.simplesolutions.medicinesmanager.repository;

import com.simplesolutions.medicinesmanager.model.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, Integer> {

    Optional<EmailConfirmation> findByToken(String token);
    @Transactional
    @Query(name = "EmailConfirmation.getLatestPatientToken")
    Optional<String> getLatestPatientToken(String patientEmail);
    boolean existsByPatientEmail(String patientEmail);
    @Transactional
    @Modifying
    @Query(name = "EmailConfirmation.updateConfirmedAt")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);
}
