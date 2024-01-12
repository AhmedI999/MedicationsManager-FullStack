package com.simplesolutions.medicinesmanager.dto.patientdto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientUpdateRequest {
    @Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
            message = "Must be a valid email address")
    final String email;
    final String currentPassword;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$",
            message = "Password should contain at least 1 uppercase and 1 special Character")
    @Size(min = 6, message = "Password Must be at least 6 characters")
    final String password;
    @Pattern(regexp = "^.+$", message = "Field can't be empty")
    final String firstname;
    @Pattern(regexp = "^.+$", message = "Field can't be empty")
    final String lastname;
    @Min(value = 1, message = " Must be at least 1")
    @Max(value = 110, message = "Please enter the correct age")
    final Integer age;
}