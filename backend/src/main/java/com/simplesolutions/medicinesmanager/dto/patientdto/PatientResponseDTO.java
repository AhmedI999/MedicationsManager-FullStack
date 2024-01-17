package com.simplesolutions.medicinesmanager.dto.patientdto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatientResponseDTO (
        Integer id,
        String email,
        String firstname,
        String lastname,
        Integer age,
        List<String> roles
) {}
