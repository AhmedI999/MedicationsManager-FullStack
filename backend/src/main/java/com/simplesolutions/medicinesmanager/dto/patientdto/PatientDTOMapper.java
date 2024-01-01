package com.simplesolutions.medicinesmanager.dto.patientdto;

import com.simplesolutions.medicinesmanager.model.Patient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PatientDTOMapper implements Function<Patient, PatientResponseDTO> {
    @Override
    public PatientResponseDTO apply(Patient patient) {
        return new PatientResponseDTO (
                patient.getId(),
                patient.getEmail(),
                patient.getFirstname(),
                patient.getLastname(),
                patient.getAge(),
                patient.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}
