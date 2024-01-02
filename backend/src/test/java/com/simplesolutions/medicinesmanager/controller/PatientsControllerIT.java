package com.simplesolutions.medicinesmanager.controller;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientUpdateRequest;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.repository.PatientRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
    PatientResponseDTO expectedPatient;
    String savePatient_getToken;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        patientRequest = new PatientRegistrationRequest(
                faker.internet().safeEmailAddress(),
                faker.internet().password() + "P@",
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().randomDigitNotZero()
        );
        expectedPatient = new PatientResponseDTO (
                null,
                patientRequest.getEmail(),
                patientRequest.getFirstname(),
                patientRequest.getLastname(),
                patientRequest.getAge(),
                List.of("ROLE_USER")
        );

        // Send a post-request to save a patient, ensuring return is 200, and retrieving the jwt token
        savePatient_getToken = Objects.requireNonNull(webTestClient.post()
                        .uri(path)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(patientRequest), PatientRegistrationRequest.class)
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult(Void.class)
                        .getResponseHeaders()
                        .get(AUTHORIZATION))
                .get(0);

    }
    @Test
    @DisplayName("Verify that savePatient endpoint return conflict status with email already exists")
    void PatientController_savePatient_conflictStatus_patientAlreadyExists()  {
            // Patient is saved in setUp()
        // save the same patient again to trigger the error
        webTestClient.post()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(patientRequest), PatientRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(DuplicateResourceException.class)
                .consumeWith(response -> {
                    DuplicateResourceException responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assertThat(responseBody.getMessage())
                            .isEqualTo("Patient with email %s already exists".formatted(patientRequest.getEmail()));
                });
    }

    @Test
    @DisplayName("Verify that savePatient and getAllPatients endPoints behaves correctly")
    void PatientController_savePatient_getAllPatients() {
            // Patient is saved in setUp()
        // get all patients
        webTestClient.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<PatientResponseDTO>() {
                }).consumeWith(response -> {
                    List<PatientResponseDTO> actualPatientList = response.getResponseBody();
                    assertThat(actualPatientList)
                            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "password")
                            .contains(expectedPatient);
                });
    }

    @Test
    @DisplayName("ensures that getPatientById returns correct patient")
    void getPatient_ById() {
            // Patient is saved in setUp()
        // Getting the id of the patient saved in the database
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        // the reason behind not including the password below is mainly for security
        webTestClient.get()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectBody(new ParameterizedTypeReference<PatientResponseDTO>() {
                })
                .consumeWith(response -> {
                    PatientResponseDTO actualPatient = response.getResponseBody();
                    assertThat(actualPatient)
                            .usingRecursiveComparison()
                            .ignoringFields("id", "password")
                            .isEqualTo(expectedPatient);
                });
    }

    @Test
    @DisplayName("Verify that deletePatient endpoint can delete patient by id")
    void deletePatient_Success() {
            // Patient is saved in setUp()
            // the patient will be used for auth only

        // Saving The patient that will be tested
        PatientRegistrationRequest patientTest = new PatientRegistrationRequest(
                faker.internet().safeEmailAddress(),
                faker.internet().password() + "P@",
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().randomDigitNotZero()
        );
        webTestClient.post()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(patientTest), PatientRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();
        // retrieving patient's id
        int patientInDB_Id = patientRepository.findByEmail(patientTest.getEmail()).orElseThrow().getId();
            //deleting the patient
        webTestClient.delete()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isOk();
        //Then
           // verifying that the patient now doesn't exist therefore is forbidden to issue requests
        webTestClient.get()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ResourceNotFoundException.class)
                .consumeWith(response -> {
                    ResourceNotFoundException responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assertThat(responseBody.getMessage())
                            .isEqualTo("patient with id %s not found".formatted(patientInDB_Id));
                });
    }

    @Test
    @DisplayName("Verify that editPatientDetails endpoint can update details")
    void editPatientDetails() {
            // Patient is saved in setUp()
        // retrieving patient's id
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        // what we are going to update
        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder().firstname("NewFirstname").build();
        // Updating
        webTestClient.put()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), PatientUpdateRequest.class)
                .exchange()
                .expectStatus().isOk();

        // confirming the new details
        webTestClient.get()
                .uri(path + "/{patientId}", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PatientResponseDTO.class)
                .consumeWith(response -> {
                    PatientResponseDTO actualPatient = response.getResponseBody();
                    assert actualPatient != null;
                    assertThat(actualPatient.getFirstname())
                                .isEqualTo(updateRequest.getFirstname());
                });
    }

}