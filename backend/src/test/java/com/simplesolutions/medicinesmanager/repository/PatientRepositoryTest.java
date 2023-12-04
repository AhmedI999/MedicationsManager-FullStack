package com.simplesolutions.medicinesmanager.repository;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.AbstractTestContainers;
import com.simplesolutions.medicinesmanager.model.Patient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("Test For Custom PatientRepository methods")
class PatientRepositoryTest extends AbstractTestContainers {
    @Autowired
    private PatientRepository patientTest;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patientTest.deleteAll();
        Faker faker = new Faker();
        patient = Patient.builder()
                .email(faker.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .password(faker.internet().password())
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .age(faker.number().randomDigitNotZero())
                .build();
    }
    @Nested
    @DisplayName("For existsPatientByEmail method")
    class PatientRepository_existsPatientByEmail {
        @Test
        @DisplayName("patient exists by valid email")
        void existsPatientByEmail_returnsTrue() {
            // Given
            patientTest.save(patient);
            //When
            boolean actual = patientTest.existsPatientByEmail(patient.getEmail());
            //Then
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("patient doesn't exist with invalid email")
        void existsPatientByEmail_returnsFalse() {
            // Given
            patientTest.save(patient);
            //When
            boolean actual = patientTest.existsPatientByEmail(patient.getEmail() + "wrong");
            //Then
            assertThat(actual).isFalse();
        }
    }
    @Nested
    @DisplayName("For findByEmail method")
    class PatientRepository_findByEmail{}
    @Test
    @DisplayName("Return patient by email")
    void findByEmail_returnsPatient() {
        // Given
        patientTest.save(patient);
        //When
        Patient actual = patientTest.findByEmail(patient.getEmail());
        //Then
        assertThat(actual).isEqualTo(patient);
    }
    @Test
    @DisplayName("Throw ResourceNotFoundException with invalid email")
    void findByEmail_throwResourceNotFoundException() {
        // Given
        patientTest.save(patient);
        //When
        Patient actual = patientTest.findByEmail(patient.getEmail() + "wrong");
        //Then
        assertThat(actual).isNull();
    }

}