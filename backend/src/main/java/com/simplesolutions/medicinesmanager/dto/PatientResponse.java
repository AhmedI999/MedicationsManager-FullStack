package com.simplesolutions.medicinesmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.simplesolutions.medicinesmanager.model.Patient;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponse {
    Integer id;
    String password;
    String email;
    String firstname;
    String lastname;
    Integer age;

    public PatientResponse(Patient patient) {
        this.id = patient.getId();
        this.password = patient.getPassword();
        this.email = patient.getEmail();
        this.firstname = patient.getFirstname();
        this.lastname = patient.getLastname();
        this.age = patient.getAge();
    }
}
