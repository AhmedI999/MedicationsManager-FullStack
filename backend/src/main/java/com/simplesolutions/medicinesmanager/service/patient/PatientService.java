package com.simplesolutions.medicinesmanager.service.patient;

import com.simplesolutions.medicinesmanager.dto.patientdto.PatientDTOMapper;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientDao patientDao;
    private final PatientDTOMapper patientDTOMapper;
    private final PasswordEncoder passwordEncoder;
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
                new ResourceNotFoundException("patient with id [%s] not found".formatted(id)));
    }
    public Patient getPatientEntityById(Integer id){
        return patientDao.selectPatientById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("patient with id [%s] not found".formatted(id)));
    }
    public PatientResponseDTO getPatientByEmail(String email){
        return patientDao.selectPatientByEmail(email)
                .map(patientDTOMapper)
                .orElseThrow(
                        () -> new ResourceNotFoundException("patient with email [%s] not found".formatted(email)));
    }
    public boolean doesPatientExists(String email) {
        return patientDao.doesPatientExists(email);
    }

    public void savePatient(PatientRegistrationRequest request){
        if (doesPatientExists(request.getEmail())) {
        throw new DuplicateResourceException("Patient with email:%s. already exists".formatted(request.getEmail()));
    }
            Patient patient = Patient.builder()
                    .email(request.getEmail().trim())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstname(request.getFirstname().trim())
                    .lastname(request.getLastname().trim())
                    .age(request.getAge())
                    .build();
            patientDao.savePatient(patient);

    }
    public void deletePatient(Integer id){
        Patient patient = patientDao.selectPatientById(id).orElseThrow(() ->
                        new ResourceNotFoundException("patient with id [%s] not found".formatted(id)));

            patientDao.deletePatientById(patient.getId());
    }
    public void editPatientDetails(Integer id, PatientUpdateRequest request){
        Patient patient = patientDao.selectPatientById(id).orElseThrow(
                        () -> new ResourceNotFoundException("patient with id [%s] not found".formatted(id)));

        boolean changes = false;
        if (request.getEmail() != null && !request.getEmail().trim().equals(patient.getEmail())) {
            if (patientDao.doesPatientExists(request.getEmail())) {
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            patient.setEmail(request.getEmail().trim());
            changes = true;
        }
        if (request.getFirstname() != null && !request.getFirstname().equals(patient.getFirstname())) {
            patient.setFirstname(request.getFirstname().trim());
            changes = true;
        }

        if (request.getLastname() != null && !request.getLastname().equals(patient.getLastname())) {
            patient.setLastname(request.getLastname().trim());
            changes = true;
        }
        if (!changes) {
            throw new UpdateException("no data changes found");
        }
        patientDao.updatePatient(patient);
    }
}
