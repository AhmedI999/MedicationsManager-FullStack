package com.simplesolutions.medicinesmanager.service.patient;

import com.simplesolutions.medicinesmanager.dto.patientdto.PatientDTOMapper;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientUpdateRequest;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.model.EmailConfirmation;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.service.patient.email.EmailConfirmationService;
import com.simplesolutions.medicinesmanager.service.patient.email.sender.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientDao patientDao;
    private final EmailConfirmationService emailService;
    private final PatientDTOMapper patientDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("#{'${authentication.email.confirmationToken.expiresAt}'}")
    private Integer MINUTES_UNTIL_CONFIRMATION_EXPIRATION;
    @Value("#{'${user.confirm-endpoint.path}'}")
    private String CONFIRMATION_ENDPOINT;
    @Value("#{'${user.confirm-endpoint.path.path-variable}'}")
    private String CONFIRMATION_PATH_VARIABLE;

    @Value("#{'${service.patient.not-found-id.message}'}")
    private String PATIENT_NOT_FOUND_MSG_ID;
    @Value("#{'${service.patient.not-found-email.message}'}")
    private String PATIENT_NOT_FOUND_MSG_EMAIL;
    @Value("#{'${service.patient.already-exists-email.message}'}")
    private String PATIENT_ALREADY_EXISTS_MSG_EMAIL;
    @Value("#{'${service.patient.password-update.current-password-null.message}'}")
    private String PATIENT_CURRENT_PASSWORD_NULL_MSG;
    @Value("#{'${service.patient.password-update.current-password-incorrect.message}'}")
    private String PATIENT_CURRENT_PASSWORD_INCORRECT_MSG;
    @Value("#{'${service.patient.password-update.new-password-null.message}'}")
    private String PATIENT_NEW_PASSWORD_NULL_MSG;
    @Value("#{'${service.patient.password-update.new-password-identical.message}'}")
    private String PATIENT_NEW_PASSWORD_IDENTICAL_MSG;
    @Value("#{'${service.patient.no-details-changes.message}'}")
    private String PATIENT_NO_CHANGES_MSG;



    public List<PatientResponseDTO> getAllPatients(){
        return patientDao.selectAllPatients()
                .stream()
                .map(patientDTOMapper)
                .collect(Collectors.toList());
    }

    public PatientResponseDTO getPatientById(Integer id){
        return patientDao.selectPatientById(id)
                .map(patientDTOMapper)
                .orElseThrow(() ->
                new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG_ID.formatted(id)));
    }
    public Patient getPatientEntityById(Integer id){
        return patientDao.selectPatientById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG_ID.formatted(id)));
    }
    public Patient getPatientEntityByEmail(String email){
        return patientDao.selectPatientByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG_EMAIL.formatted(email)));
    }
    public PatientResponseDTO getPatientByEmail(String email){
        return patientDao.selectPatientByEmail(email)
                .map(patientDTOMapper)
                .orElseThrow(
                        () -> new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG_EMAIL.formatted(email)));
    }
    public void verifyPatientAlreadyExist(String email) {
        if (patientDao.doesPatientExists(email)){
            throw new DuplicateResourceException(PATIENT_ALREADY_EXISTS_MSG_EMAIL.formatted(email));
        }
    }

    public void createAndSendEmailVerification(String email) {
        Patient patient = getPatientEntityByEmail(email);

        String emailToken = emailService.CreateConfirmationRequest(patient);
        String emailBuild =  buildEmail(patient.getFirstname(),
                CONFIRMATION_ENDPOINT + CONFIRMATION_PATH_VARIABLE + emailToken);

        emailSender.sendVerification(patient.getEmail(), emailBuild);
    }
    public void savePatient(PatientRegistrationRequest request){
        verifyPatientAlreadyExist(request.email());
        Patient patient = Patient.builder()
                .email(request.email().trim())
                .password(passwordEncoder.encode(request.password()))
                .firstname(request.firstname().trim())
                .lastname(request.lastname())
                .age(request.age())
                .build();

        patientDao.savePatient(patient);
        createAndSendEmailVerification(patient.getEmail());
    }

    public void confirmEmail(String token){
        EmailConfirmation emailConfirmation = emailService.getEmailConfirmation(token);
        emailService.validateEmailConfirmation(token);
        emailService.setTokenConfirmation(token);
        enablePatientAccount(emailConfirmation.getPatient().getEmail());
    }
    public void enablePatientAccount(String email){
        if (!patientDao.doesPatientExists(email)){
            throw new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG_EMAIL.formatted(email));
        }
        patientDao.enablePatientAccount(email);
    }
    public void deletePatient(Integer id){
        Patient patient = patientDao.selectPatientById(id).orElseThrow(() ->
                        new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG_ID.formatted(id)));

            patientDao.deletePatientById(patient.getId());
    }
    protected boolean isOldPasswordValid ( String currentPassword, Patient patient) {
        if (currentPassword == null || currentPassword.isEmpty()){
            throw new UpdateException(PATIENT_CURRENT_PASSWORD_NULL_MSG);
        }
        return passwordEncoder.matches(currentPassword, patient.getPassword());
    }
    protected boolean isOldAndNewPasswordIdentical (String newPassword, Patient patient) {
        return passwordEncoder.matches(newPassword, patient.getPassword());
    }
    public void editPatientPassword(Integer id, PatientUpdateRequest request){
        Patient patient = patientDao.selectPatientById(id).orElseThrow(
                () -> new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG_ID.formatted(id)));
        if (isOldPasswordValid(request.getCurrentPassword(), patient)){
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new UpdateException(PATIENT_NEW_PASSWORD_NULL_MSG);
            }
            if (isOldAndNewPasswordIdentical(request.getPassword(), patient)){
                throw new DuplicateResourceException(PATIENT_NEW_PASSWORD_IDENTICAL_MSG);
            }
            patient.setPassword(passwordEncoder.encode(request.getPassword()));
            patientDao.updatePatient(patient);
        } else throw new UpdateException(PATIENT_CURRENT_PASSWORD_INCORRECT_MSG);
    }
    public void editPatientDetails(Integer id, PatientUpdateRequest request){
        Patient patient = patientDao.selectPatientById(id).orElseThrow(
                        () -> new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG_ID.formatted(id)));
        boolean changes = false;
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!Objects.equals(patient.getEmail(), request.getEmail()) ) {
                if (patientDao.doesPatientExists(request.getEmail())) {
                    throw new DuplicateResourceException(PATIENT_ALREADY_EXISTS_MSG_EMAIL);
                }
                patient.setEmail(request.getEmail().trim());
                changes = true;
            }
        }
        if (request.getFirstname() != null && !request.getFirstname().equals(patient.getFirstname())) {
            patient.setFirstname(request.getFirstname().trim());
            changes = true;
        }

        if (request.getLastname() != null && !request.getLastname().equals(patient.getLastname())) {
            patient.setLastname(request.getLastname());
            changes = true;
        }
        if (request.getAge() != null && !request.getAge().equals(patient.getAge())) {
            patient.setAge(request.getAge());
            changes = true;
        }

        if (!changes) {
            throw new UpdateException(PATIENT_NO_CHANGES_MSG);
        }
        patientDao.updatePatient(patient);
    }

    private String buildEmail(String name, String link) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("link", link);
        context.setVariable("minutesUntilConfirmationExpiration", MINUTES_UNTIL_CONFIRMATION_EXPIRATION);
        return templateEngine.process("email-template", context);
    }
}
