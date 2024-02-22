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
    @Value("#{'${jwt.email.confirmationToken.expiresAt}'}")
    private Integer MINUTES_UNTIL_CONFIRMATION_EXPIRATION;

    private String CONFIRMATION_ENDPOINT = "http://localhost:8080/api/v1/auth/confirm";
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
                new ResourceNotFoundException("patient with id %s not found".formatted(id)));
    }
    public Patient getPatientEntityById(Integer id){
        return patientDao.selectPatientById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("patient with id %s not found".formatted(id)));
    }
    public Patient getPatientEntityByEmail(String email){
        return patientDao.selectPatientByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("patient with email %s was not found".formatted(email)));
    }
    public PatientResponseDTO getPatientByEmail(String email){
        return patientDao.selectPatientByEmail(email)
                .map(patientDTOMapper)
                .orElseThrow(
                        () -> new ResourceNotFoundException("patient with email %s was not found".formatted(email)));
    }
    public void verifyPatientAlreadyExist(String email) {
        if (patientDao.doesPatientExists(email)){
            throw new DuplicateResourceException("Patient with email %s already exists".formatted(email));
        }
    }

    public void createAndSendEmailVerification(String email) {
        Patient patient = getPatientEntityByEmail(email);

        String emailToken = emailService.CreateConfirmationRequest(patient);
        String emailBuild =  buildEmail(patient.getFirstname() + " " + patient.getLastname(),
                CONFIRMATION_ENDPOINT + "?token=" + emailToken);

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
            throw new ResourceNotFoundException("patient with email %s was not found".formatted(email));
        }
        patientDao.enablePatientAccount(email);
    }
    public void deletePatient(Integer id){
        Patient patient = patientDao.selectPatientById(id).orElseThrow(() ->
                        new ResourceNotFoundException("patient with id %s not found".formatted(id)));

            patientDao.deletePatientById(patient.getId());
    }
    protected boolean isOldPasswordValid ( String currentPassword, Patient patient) {
        if (currentPassword == null || currentPassword.isEmpty()){
            throw new UpdateException("Current password can't be null or empty");
        }
        return passwordEncoder.matches(currentPassword, patient.getPassword());
    }
    protected boolean isOldAndNewPasswordIdentical (String newPassword, Patient patient) {
        return passwordEncoder.matches(newPassword, patient.getPassword());
    }
    public void editPatientPassword(Integer id, PatientUpdateRequest request){
        Patient patient = patientDao.selectPatientById(id).orElseThrow(
                () -> new ResourceNotFoundException("patient with id %s not found".formatted(id)));
        if (isOldPasswordValid(request.getCurrentPassword(), patient)){
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new UpdateException("New password Can't be empty");
            }
            if (isOldAndNewPasswordIdentical(request.getPassword(), patient)){
                throw new DuplicateResourceException("Passwords are identical");
            }
            patient.setPassword(passwordEncoder.encode(request.getPassword()));
            patientDao.updatePatient(patient);
        } else throw new UpdateException("Current password is incorrect");
    }
    public void editPatientDetails(Integer id, PatientUpdateRequest request){
        Patient patient = patientDao.selectPatientById(id).orElseThrow(
                        () -> new ResourceNotFoundException("patient with id %s not found".formatted(id)));
        boolean changes = false;
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!Objects.equals(patient.getEmail(), request.getEmail()) ) {
                if (patientDao.doesPatientExists(request.getEmail())) {
                    throw new DuplicateResourceException(
                            "email already registered"
                    );
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
            throw new UpdateException("no data changes found");
        }
        patientDao.updatePatient(patient);
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in " + MINUTES_UNTIL_CONFIRMATION_EXPIRATION + " minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
