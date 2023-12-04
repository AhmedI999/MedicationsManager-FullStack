package com.simplesolutions.medicinesmanager.controller;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.paylod.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.paylod.PatientUpdateRequest;
import com.simplesolutions.medicinesmanager.repository.PatientRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Patients controller Integration Tests")
class PatientsControllerIT {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    PatientRepository patientRepository;
    private static final String path = "/api/v1/patients";
    Faker faker;
    PatientRegistrationRequest patientRequest;
    Patient expectedPatient;
    StatusAssertions savePatientStatusAssertions;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        patientRequest = generateUniquePatientRequest();
        expectedPatient = Patient.builder()
                .email(patientRequest.getEmail())
                .password(patientRequest.getPassword())
                .firstname(patientRequest.getFirstname())
                .lastname(patientRequest.getLastname())
                .age(patientRequest.getAge())
                .patientMedicines(new ArrayList<>())
                .build();

        // webTestClientRequest called to save Patient
        savePatientStatusAssertions = webTestClient.post()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(patientRequest), PatientRegistrationRequest.class)
                .exchange()
                .expectStatus();

    }

    // to ensure that every registration request hava unique email
    private PatientRegistrationRequest generateUniquePatientRequest(){
        Set<String> generatedEmails = new HashSet<>();
        String email;
        // generating unique email
        do {
            email = faker.internet().safeEmailAddress();
        } while (!generatedEmails.add(email) && !patientRepository.existsPatientByEmail(email));

        return new PatientRegistrationRequest(
                email,
                faker.internet().password() + "P@",
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().randomDigitNotZero()
        );
    }

    @Test
    @DisplayName("Verify that savePatient and getAllPatients endPoints behaves correctly")
    void PatientController_savePatient_getAllPatients() {
        // Send a post request to save a patient and ensuring return is 200
        savePatientStatusAssertions.isOk();
        // get all patients
        List<Patient> allPatients = webTestClient.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Patient>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that patient is present
        assertThat(allPatients)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedPatient);
    }

    @Test
    @DisplayName("ensures that getPatientById returns correct patient")
    void getPatient_ById() {
        // Given
        savePatientStatusAssertions.isOk();
        //When
            // Getting the id of the patient saved in the database
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        //Then
            // reason behind not including password below is mainly for security
        webTestClient.get()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<Patient>() {
                })
                .consumeWith(response -> {
                    Patient actualPatient = response.getResponseBody();
                    assertThat(actualPatient)
                            .usingRecursiveComparison()
                            .ignoringFields("id", "password")
                            .isEqualTo(expectedPatient);
                });
    }

    @Test
    @DisplayName("Verify that deletePatient endpoint can delete patient by id")
    void deletePatient_Success() {
        // Given
            //creating a patient and saving it
        savePatientStatusAssertions.isOk();
        //When
            // retrieving patient's id
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
            //deleting the patient
        webTestClient.delete()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
        //Then
           // verifying that the patient now doesn't exist
        webTestClient.get()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Verify that editPatientDetails endpoint can update details")
    void editPatientDetails() {
        // Given
            //creating a patient and saving it
        savePatientStatusAssertions.isOk();
        //When
            // retrieving patient's id
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
            // what we are going to update
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder().firstname("NewFirstname").build();
            // Updating
        webTestClient.put()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), PatientUpdateRequest.class)
                .exchange()
                .expectStatus().isOk();
        //Then
            // confirming the new details
        webTestClient.get()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Patient.class)
                .consumeWith(response -> {
                    Patient actualPatient = response.getResponseBody();
                        assertThat(actualPatient.getFirstname())
                                .isEqualTo(updateRequest.getFirstname());
                });
    }

}