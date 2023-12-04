package com.simplesolutions.medicinesmanager.paylod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientRegistrationRequest {
    @NotBlank(message = "Field is Required")
    @Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
            message = "Must be a valid email address")
    final String email;
    @NotBlank(message = "Field is Required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$",
            message = "Password should contain at least 1 uppercase and 1 special Character")
    @Size(min = 6, message = "Password Must be at least 6 characters")
    final String password;
    final String firstname;
    final String lastname;
    final int age;
}
