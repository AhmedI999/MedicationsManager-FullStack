package com.simplesolutions.medicinesmanager.controller;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.dto.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.dto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.repository.MedicineRepository;
import com.simplesolutions.medicinesmanager.repository.PatientRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Medicine controller Integration Tests")
class PatientMedicinesControllerIT {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    MedicineRepository medicineRepository;
    // Mapping for the controller
    static final String path = "/api/v1/patients";
    Faker faker;
    MedicineRegistrationRequest medicineRequest;
    Medicine expectedMedicine;
    Patient expectedPatient;
    PatientRegistrationRequest patientRequest;
    StatusAssertions savePatientStatusAssertions;
    @Value("#{'${medicine.picture-url}'}")
    String DEFAULT_PICTURE_URL;
    StatusAssertions saveMedicineStatusAssertions;


    @BeforeEach
    void setUp() {
        faker = new Faker();
        // generate patientRegistrationRequest with unique email every cal
        patientRequest = generateUniquePatientRequest();
        expectedPatient  = Patient.builder()
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

        // generate medicineRegistrationRequest with unique brandName every call
        medicineRequest = new MedicineRegistrationRequest(
                DEFAULT_PICTURE_URL,
                "U" + faker.lorem().word(),
                faker.lorem().characters(10),
                faker.random().nextInt(1, 5),
                faker.lorem().characters());

        // expected Medicine To return
        expectedMedicine = Medicine.builder()
                .pictureUrl(medicineRequest.getPictureUrl())
                .brandName(medicineRequest.getBrandName())
                .activeIngredient(medicineRequest.getActiveIngredient())
                .timesDaily(medicineRequest.getTimesDaily())
                .instructions(medicineRequest.getInstructions())
                .interactions(new ArrayList<>())
                .patient(expectedPatient)
                .build();
        // webTestClientRequest called to save Medicine
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        saveMedicineStatusAssertions = webTestClient.post()
                .uri(path + "/{patientId}/medicines", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(medicineRequest), MedicineRegistrationRequest.class)
                .exchange().expectStatus();





    }
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
    @DisplayName("Ensure that savePatientMedicine endpoint can save a medicine and add it to Patient")
    void savePatientMedicine() {
        // saving patient
        savePatientStatusAssertions.isOk();
        // saving medicine and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        expectedPatient.setId(patientInDB_Id);
        saveMedicineStatusAssertions.isOk();
    }

    @Test
    @DisplayName("ensure that getMedicine can retrieve medicine by patient and medicine id")
    void getMedicine_saveMedicine_retrieveById() {
        // saving patient
        savePatientStatusAssertions.isOk();
        // saving medicine and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        saveMedicineStatusAssertions.isOk();
        // getting the saved medicine id
        int medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, expectedMedicine.getBrandName()).getId();
        // Retrieving the medicine
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/id/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<Medicine>() {
                })
                .consumeWith(response -> {
                    Medicine actualMedicine = response.getResponseBody();
                    assertThat(actualMedicine)
                            .usingRecursiveComparison()
                            // medicine number is automatically created like id so no need to check it
                            .ignoringFields("id", "medicineNumber", "patient")
                            .isEqualTo(expectedMedicine);
                });
    }
    @Test
    @DisplayName("ensure that getMedicineByBrandName can retrieve medicine by patient id and brandName")
    void getMedicine_saveMedicine_retrieveByBrandName() {
        // saving patient
        savePatientStatusAssertions.isOk();
        // saving medicine and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        saveMedicineStatusAssertions.isOk();
        // Retrieving the medicine
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/{brandName}", patientInDB_Id, medicineRequest.getBrandName())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<Medicine>() {
                })
                .consumeWith(response -> {
                    Medicine actualMedicine = response.getResponseBody();
                    assertThat(actualMedicine)
                            .usingRecursiveComparison()
                            // medicine number is automatically created like id so no need to check it
                            .ignoringFields("id", "medicineNumber", "patient")
                            .isEqualTo(expectedMedicine);
                });
    }

    @Test
    @DisplayName("Verify that getAllPatientMedicines endPoint can retrieve all medicines")
    void getAllPatientMedicines() {
        savePatientStatusAssertions.isOk();
        // saving medicine and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        saveMedicineStatusAssertions.isOk();
        // get all patient medicines
        List<Medicine> allMedicines = webTestClient.get()
                .uri(path + "/{patientId}/medicines", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Medicine>() {
                })
                .returnResult().getResponseBody();
        assertThat(allMedicines)
                // medicine number is automatically created like id so no need to check it
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "medicineNumber", "patient")
                .contains(expectedMedicine);
    }
    @Test
    @DisplayName("Verify that deleteMedicine endPoint can delete PatientMedicine")
    void deleteMedicine() {
        savePatientStatusAssertions.isOk();
        // saving medicine and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        saveMedicineStatusAssertions.isOk();
        // getting the saved medicine id
        int medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, expectedMedicine.getBrandName()).getId();
        // deleting the medicine
        webTestClient.delete()
                .uri(path + "/{patientId}/medicines/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
        // verifying that the medicine now doesn't exist
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

    }
    @Test
    @DisplayName("Verify that editMedicineDetails endpoint can update details")
    void editMedicineDetails() {
        savePatientStatusAssertions.isOk();
        // saving medicine and verifying that response is 200
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        saveMedicineStatusAssertions.isOk();
        // getting the saved medicine id
        int medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, expectedMedicine.getBrandName()).getId();
        // what we are going to update in medicine
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
                .body(Mono.just(updateRequest), MedicineUpdateRequest.class)
                .exchange()
                .expectStatus().isOk();
        // confirming the new details
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/id/{medicineId}", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Medicine.class)
                .consumeWith(response -> {
                    Medicine actualMedicine = response.getResponseBody();
                        assertThat(actualMedicine.getActiveIngredient())
                                .isEqualTo(updateRequest.getActiveIngredient());
                    assertThat(actualMedicine.getPictureUrl())
                            .isEqualTo(updateRequest.getPictureUrl());
                });
    }


}