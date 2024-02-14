package com.simplesolutions.medicinesmanager.dto.patientdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientRegistrationRequest(
        @NotBlank(message = "Field is Required")
        @Pattern(regexp = "^[_A-Za-z0-9]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
                message = "Must be a valid email address")
        String email,
        @NotBlank(message = "Field is Required")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$",
                message = "Password should contain at least 1 uppercase and 1 special Character")
        @Size(min = 6, message = "Password Must be at least 6 characters")
        String password,
        @NotBlank(message = "Field is Required")
        @Pattern(regexp = "^[A-Za-z]+$", message = "Only alphabetic characters are allowed")
        String firstname,
        @Pattern(regexp = "^[A-Za-z]*$", message = "Only alphabetic characters are allowed")
        String lastname,
        @Min(value = 1, message = "Age Must be at least 1")
        Integer age)
{}
