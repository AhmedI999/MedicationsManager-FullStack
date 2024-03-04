package com.simplesolutions.medicinesmanager.service.patient.email;

import com.simplesolutions.medicinesmanager.exception.ConfirmationRequestExpiredException;
import com.simplesolutions.medicinesmanager.exception.EmailAlreadyVerifiedException;
import com.simplesolutions.medicinesmanager.exception.OwnershipException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.EmailConfirmation;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EmailConfirmationService {
    private final EmailConfirmationDao emailConfirmationDao;
    private final JWTUtil jwtUtil;
    @Value("#{'${authentication.email.confirmationToken.expiresAt}'}")
    private Integer MINUTES_UNTIL_CONFIRMATION_EXPIRATION;

    @Value("#{'${service.email-confirmation.not-found.message}'}")
    private String EMAIL_CONFIRMATION_NOT_FOUND;
    @Value("#{'${service.email-confirmation.already-verified.message}'}")
    private String EMAIL_CONFIRMATION_ALREADY_VERIFIED;
    @Value("#{'${service.email-confirmation.confirmation-expired.message}'}")
    private String EMAIL_CONFIRMATION_REQUEST_EXPIRED;
    @Value("#{'${service.email-confirmation.wrong-owner.message}'}")
    private String EMAIL_CONFIRMATION_WRONG_OWNER;
    @Value("#{'${service.email-confirmation.token.not-found.message}'}")
    private String EMAIL_CONFIRMATION_TOKEN_NOT_FOUND;


    public void setTokenConfirmation(String token){
        emailConfirmationDao.setTokenConfirmedAt(token);
    }

    public EmailConfirmation getEmailConfirmation(String token) {
        return emailConfirmationDao.selectEmailConfirmation(token)
                .orElseThrow(() -> new ResourceNotFoundException(EMAIL_CONFIRMATION_NOT_FOUND));
    }

    private void validateEmailAlreadyVerified(EmailConfirmation emailConfirmation){
        if (emailConfirmation.getConfirmedAt() != null || emailConfirmation.getPatient().isEnabled()){
            throw new EmailAlreadyVerifiedException(EMAIL_CONFIRMATION_ALREADY_VERIFIED);
        }
    }
    private void validateEmailConfirmationExpiration (EmailConfirmation emailConfirmation){
        if (LocalDateTime.now().isAfter(emailConfirmation.getExpiresAt())){
            throw new ConfirmationRequestExpiredException(EMAIL_CONFIRMATION_REQUEST_EXPIRED);
        }
    }
    private void validateEmailConfirmationOwnership(EmailConfirmation emailConfirmation){
        if (!emailConfirmationDao.doesPatientEmailConfirmationExists(emailConfirmation.getPatient())){
            throw new OwnershipException(EMAIL_CONFIRMATION_WRONG_OWNER);
        }
    }

    public void validateEmailConfirmation(String token){
        EmailConfirmation emailConfirmation = emailConfirmationDao.selectEmailConfirmation(token).orElseThrow();
        validateEmailAlreadyVerified(emailConfirmation);
        validateEmailConfirmationExpiration(emailConfirmation);
        validateEmailConfirmationOwnership(emailConfirmation);
    }
    
    public String CreateConfirmationRequest(Patient patient){
        String confirmationToken = jwtUtil.issueEmailToken(patient.getEmail());
        EmailConfirmation emailConfirmation = EmailConfirmation.builder()
                .token(confirmationToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(MINUTES_UNTIL_CONFIRMATION_EXPIRATION))
                .build();

        emailConfirmation.setPatient(patient);
        validateEmailAlreadyVerified(emailConfirmation);
        emailConfirmationDao.saveEmailConfirmation(emailConfirmation);
        return confirmationToken;
    }

    public String getPatientLatestEmailToken (String patientEmail){
        return emailConfirmationDao.selectPatientLatestEmailToken(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException(EMAIL_CONFIRMATION_TOKEN_NOT_FOUND));
    }

}
