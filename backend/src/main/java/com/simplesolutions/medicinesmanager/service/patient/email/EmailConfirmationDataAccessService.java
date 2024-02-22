package com.simplesolutions.medicinesmanager.service.patient.email;

import com.simplesolutions.medicinesmanager.model.EmailConfirmation;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.repository.EmailConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class EmailConfirmationDataAccessService implements EmailConfirmationDao {

    private final EmailConfirmationRepository confirmationRepository;
    @Override
    public void saveEmailConfirmation(EmailConfirmation emailConfirmation) {
        confirmationRepository.save(emailConfirmation);
    }

    @Override
    public Optional<EmailConfirmation> selectEmailConfirmation(String token) {
        return confirmationRepository.findByToken(token);
    }

    @Override
    public Optional<String> selectPatientLatestEmailToken(String email) {
        return confirmationRepository.getLatestPatientToken(email);
    }

    @Override
    public int setTokenConfirmedAt(String token) {
        return confirmationRepository.updateConfirmedAt(token, LocalDateTime.now());
    }

    @Override
    public boolean doesPatientEmailConfirmationExists(Patient patient) {
        return confirmationRepository.existsByPatientEmail(patient.getEmail());
    }
}
