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
    @Query("SELECT c.token FROM EmailConfirmation c WHERE c.patient.email = ?1 ORDER BY c.createdAt DESC")
    Optional<String> getLatestPatientToken(String patientEmail);
    boolean existsByPatientEmail(String patientEmail);
    @Transactional
    @Modifying
    @Query("UPDATE EmailConfirmation c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);
}
