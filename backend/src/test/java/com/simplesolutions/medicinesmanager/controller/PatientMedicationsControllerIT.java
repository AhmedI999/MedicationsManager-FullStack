package com.simplesolutions.medicinesmanager.controller;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicationResponseDTO;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.repository.MedicineRepository;
import com.simplesolutions.medicinesmanager.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Medication controller Integration Tests")
@Transactional
class PatientMedicationsControllerIT {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    MedicineRepository medicineRepository;
    // Mapping for the controller
    static final String path = "/api/v1/patients";
    Faker faker;
    PatientRegistrationRequest patientRequest;
    MedicineRegistrationRequest medicineRequest;
    PatientResponseDTO expectedPatient;
    MedicationResponseDTO expectedMedication;
    String savePatient_getToken;
    @Value("#{'${medicine.picture-url}'}")
    String DEFAULT_PICTURE_URL;
    StatusAssertions saveMedicineStatusAssertions;


    @BeforeEach
    void setUp() {
        faker = new Faker();
        // generate patientRegistrationRequest
        patientRequest = new PatientRegistrationRequest(
                faker.internet().safeEmailAddress(),
                faker.internet().password() + "P@",
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().randomDigitNotZero()
        );

        expectedPatient = new PatientResponseDTO(
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

        // generate medicineRegistrationRequest
        medicineRequest = new MedicineRegistrationRequest(
                DEFAULT_PICTURE_URL,
                "U" + faker.lorem().word(),
                faker.lorem().characters(10),
                faker.random().nextInt(1, 5),
                faker.lorem().characters());

        // expected Medication To return
        expectedMedication = new MedicationResponseDTO(
                null,
                null,
                medicineRequest.getPictureUrl(),
                medicineRequest.getBrandName(),
                medicineRequest.getActiveIngredient(),
                medicineRequest.getTimesDaily(),
                medicineRequest.getInstructions(),
                new ArrayList<>()
        );
        // webTestClientRequest called to save Medication
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        saveMedicineStatusAssertions = webTestClient.post()
                .uri(path + "/{patientId}/medicines", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(medicineRequest), MedicineRegistrationRequest.class)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange().expectStatus();





    }

    @Test
    @DisplayName("Ensure that savePatientMedicine endpoint can save a medication and add it to Patient")
    void savePatientMedicine() {
            // Patient is saved in setUp()
        // saving medication and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        expectedPatient.setId(patientInDB_Id);
        saveMedicineStatusAssertions.isOk();
    }

    @Test
    @DisplayName("ensure that getMedication can retrieve medication by patient and medication id")
    void getMedicine_saveMedicine_retrieveById() {
            // Patient is saved in setUp()
        // saving medication and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        saveMedicineStatusAssertions.isOk();
        // getting the saved medication id
        int medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, expectedMedication.getBrandName()).orElseThrow().getId();
        // Retrieving the medication
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/id/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectBody(new ParameterizedTypeReference<MedicationResponseDTO>() {
                })
                .consumeWith(response -> {
                    MedicationResponseDTO actualMedication = response.getResponseBody();
                    assertThat(actualMedication)
                            .usingRecursiveComparison()
                            // medication number is automatically created like id so no need to check it
                            .ignoringFields("id", "medicineNumber", "patient")
                            .isEqualTo(expectedMedication);
                });
    }
    @Test
    @DisplayName("ensure that getMedicineByBrandName can retrieve medication by patient id and brandName")
    void getMedicine_saveMedicine_retrieveByBrandName() {
            // Patient is saved in setUp()
        // saving medication and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        saveMedicineStatusAssertions.isOk();
        // Retrieving the medication
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/{brandName}", patientInDB_Id, medicineRequest.getBrandName())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectBody(new ParameterizedTypeReference<MedicationResponseDTO>() {
                })
                .consumeWith(response -> {
                    MedicationResponseDTO actualMedication = response.getResponseBody();
                    assertThat(actualMedication)
                            .usingRecursiveComparison()
                            // medication number is automatically created like id so no need to check it
                            .ignoringFields("id", "medicineNumber", "patient")
                            .isEqualTo(expectedMedication);
                });
    }

    @Test
    @DisplayName("Verify that getAllPatientMedicines endPoint can retrieve all medicines")
    void getAllPatientMedicines() {
            // Patient is saved in setUp()
        // saving medication and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        saveMedicineStatusAssertions.isOk();
        // get all patient medicines
        List<MedicationResponseDTO> allMedications = webTestClient.get()
                .uri(path + "/{patientId}/medicines", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<MedicationResponseDTO>() {
                })
                .returnResult().getResponseBody();
        assertThat(allMedications)
                // medication number is automatically created like id so no need to check it
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "medicineNumber", "patient")
                .contains(expectedMedication);
    }
    @Test
    @DisplayName("Verify that deleteMedicine endPoint can delete PatientMedicine")
    void deleteMedicine() {
            // Patient is saved in setUp()
        // saving medication and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        saveMedicineStatusAssertions.isOk();
        // getting the saved medication id
        int medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, expectedMedication.getBrandName()).orElseThrow().getId();
        // deleting the medication
        webTestClient.delete()
                .uri(path + "/{patientId}/medicines/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isOk();
        // verifying that the medication now doesn't exist
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ResourceNotFoundException.class)
                .consumeWith(response -> {
                    ResourceNotFoundException responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assertThat(responseBody.getMessage())
                            .isEqualTo("Medication %s wasn't found".formatted(medicineInDB_Id));
                });

    }
    @Test
    @DisplayName("Verify that editMedicineDetails endpoint can update details")
    void editMedicineDetails() {
            // Patient is saved in setUp()
        // saving medication and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        saveMedicineStatusAssertions.isOk();
        // getting the saved medication id
        int medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, expectedMedication.getBrandName()).orElseThrow().getId();
        // what we are going to update in medication
        // adding editing pictureUrl to test it
        MedicineUpdateRequest updateRequest = MedicineUpdateRequest.builder()
                .activeIngredient("Love")
                .pictureUrl("https://i.imgur.com/qmgE3uS.jpeg")
                .build();
        // updating
        webTestClient.put()
                .uri(path + "/{patientId}/medicines/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .body(Mono.just(updateRequest), MedicineUpdateRequest.class)
                .exchange()
                .expectStatus().isOk();
        // confirming the new details
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/id/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MedicationResponseDTO.class)
                .consumeWith(response -> {
                    MedicationResponseDTO actualMedication = response.getResponseBody();
                    assert actualMedication != null;
                    assertThat(actualMedication.getActiveIngredient())
                                .isEqualTo(updateRequest.getActiveIngredient());
                    assertThat(actualMedication.getPictureUrl())
                            .isEqualTo(updateRequest.getPictureUrl());
                });
    }


}