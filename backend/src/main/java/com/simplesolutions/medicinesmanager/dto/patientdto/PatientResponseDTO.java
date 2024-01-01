package com.simplesolutions.medicinesmanager.dto.patientdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponseDTO {
    Integer id;
    String email;
    String firstname;
    String lastname;
    Integer age;
    List<String> roles;
}
