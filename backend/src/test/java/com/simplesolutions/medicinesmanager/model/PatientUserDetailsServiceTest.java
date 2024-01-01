package com.simplesolutions.medicinesmanager.model;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.service.patient.PatientDao;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class PatientUserDetailsServiceTest {
    @Mock
    PatientDao patientDao;
    PatientUserDetailsService userDetailsService;
    Patient patient;

    @BeforeEach
    void setUp() {
        userDetailsService = new PatientUserDetailsService(patientDao);
        Faker faker = new Faker();
        patient = Patient.builder()
                .email(faker.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .password(faker.internet().password())
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .age(faker.number().randomDigitNotZero())
                .build();

    }

    @Test
    @DisplayName("Ensure that UserDetails can retrieve patient by email")
    void loadUserByUsername_returnPatient() {
        // Given
        when(patientDao.selectPatientByEmail(patient.getEmail())).thenReturn(Optional.of(patient));
        //When
        UserDetails userDetails = userDetailsService.loadUserByUsername(patient.getEmail());
        //Then
        verify(patientDao).selectPatientByEmail(patient.getEmail());
        assertThat(userDetails).isNotNull();

    }

    @Test
    @DisplayName("Ensure that UserDetails throw UsernameNotFoundException with invalid email")
    void loadUserByUsername() {
        // Given
        when(patientDao.selectPatientByEmail(patient.getEmail())).thenReturn(Optional.empty());
        //When
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(patient.getEmail()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("patient with email [%s] not found".formatted(patient.getEmail()));
        //Then
        verify(patientDao).selectPatientByEmail(patient.getEmail());
    }
}