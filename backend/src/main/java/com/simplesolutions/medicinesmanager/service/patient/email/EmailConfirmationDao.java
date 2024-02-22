package com.simplesolutions.medicinesmanager.service.patient.email;

import com.simplesolutions.medicinesmanager.model.EmailConfirmation;
import com.simplesolutions.medicinesmanager.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmationDao {
    void saveEmailConfirmation(EmailConfirmation emailConfirmation);
    Optional<EmailConfirmation> selectEmailConfirmation(String token);
    Optional<String> selectPatientLatestEmailToken(String email);
    int setTokenConfirmedAt(String token);
    boolean doesPatientEmailConfirmationExists(Patient patient);

}
