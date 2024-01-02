package com.simplesolutions.medicinesmanager.controller;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.dto.interactiondto.MedicationInteractionDTO;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicationResponseDTO;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.model.InteractionType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Medication Interactions controller Integration Tests")
class MedicationInteractionsControllerTest {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    MedicineRepository medicineRepository;
    @Autowired
    PatientRepository patientRepository;

    static final String path = "/api/v1/patients";
    @Value("#{'${medicine.picture-url}'}")
    String DEFAULT_PICTURE_URL;
    Faker faker;
    MedicationInteractionDTO interactionRequest;
    MedicationInteractionDTO expectedInteraction;
    MedicineRegistrationRequest medicineRequest;
    MedicationResponseDTO expectedMedication;
    int medicineInDB_Id;
    PatientRegistrationRequest patientRequest;
    PatientResponseDTO expectedPatient;
    int patientInDB_Id;

    // Patient and Medication Response spec
    String savePatient_getToken;
    // Interaction Status assertion
    StatusAssertions saveInteractionStatusAssertion;


    @BeforeEach
    void setUp() {
        faker = new Faker();
        // Patient
        patientRequest = new PatientRegistrationRequest(
                faker.internet().safeEmailAddress(),
                faker.internet().password() + "P@",
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().randomDigitNotZero());
        expectedPatient = new PatientResponseDTO(
                null,
                patientRequest.getEmail(),
                patientRequest.getFirstname(),
                patientRequest.getLastname(),
                patientRequest.getAge(),
                List.of("ROLE_USER"));
        // Interaction
        Random random = new Random();
        interactionRequest = new MedicationInteractionDTO(
                "U" + faker.lorem().word(),
                InteractionType.values()[random.nextInt(InteractionType.values().length)]
        );
        expectedInteraction = new MedicationInteractionDTO(
                interactionRequest.getName(),
                interactionRequest.getType()
        );
        // Medication
        medicineRequest = new MedicineRegistrationRequest(
                DEFAULT_PICTURE_URL,
                "U" + faker.lorem().word(),
                faker.lorem().characters(10),
                faker.random().nextInt(1, 5),
                faker.lorem().characters());

        expectedMedication = new MedicationResponseDTO(
                null,
                medicineRequest.getPictureUrl(),
                medicineRequest.getBrandName(),
                medicineRequest.getActiveIngredient(),
                medicineRequest.getTimesDaily(),
                medicineRequest.getInstructions(),
                new ArrayList<>()
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

        // Retrieving Patient id from Database
        patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();


        // Saving Medication
        webTestClient.post()
                .uri(path + "/{patientId}/medicines", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(medicineRequest), MedicineRegistrationRequest.class)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isOk();

        // Retrieving Medication id from Database
        medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, medicineRequest.getBrandName()).orElseThrow().getId();

        // Interaction Status assertion
        saveInteractionStatusAssertion = webTestClient.post()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(interactionRequest), MedicationInteractionDTO.class)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange().expectStatus();

    }

    @Test
    @DisplayName("Ensure that saveMedicationInteraction can save interaction successfully to specific medication")
    void saveMedicationInteraction() {
        // Saving interaction to the medication and verifying the result is 200
        saveInteractionStatusAssertion.isOk();
    }

    @Test
    @DisplayName("Ensure that getMedicationInteraction endPoint can retrieve interaction by name")
    void getMedicationInteraction() {
        // saving interaction
        saveInteractionStatusAssertion.isOk();
        // Retrieving Patient, Medication, and Interaction id from Database
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).orElseThrow().getId();
        int medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, medicineRequest.getBrandName()).orElseThrow().getId();
        // Retrieving the interaction
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions/{name}",
                        patientInDB_Id, medicineInDB_Id, interactionRequest.getName())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectBody(new ParameterizedTypeReference<MedicationInteractionDTO>() {
                })
                .consumeWith(response -> {
                    MedicationInteractionDTO actualInteraction = response.getResponseBody();
                    assertThat(actualInteraction)
                            .usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(expectedInteraction);
                });
    }

    @Test
    @DisplayName("Ensure that getAllMedicationInteractions endPoint can retrieve interactions ")
    void getAllMedicationInteractions() {
        // Saving Interaction for medication
        saveInteractionStatusAssertion.isOk();
        // Retrieving List of interactions
        List<MedicationInteractionDTO> allMedicationInteractions = webTestClient.get()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<MedicationInteractionDTO>() {
                })
                .returnResult().getResponseBody();
        // Verifying that the saved interaction is the same as the expected one
        assertThat(allMedicationInteractions)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedInteraction);
    }

    @Test
    @DisplayName("Ensure that deleteMedicationInteraction endPoint can delete interaction by name")
    void deleteMedicationInteraction() {
        // Saving Interaction for medication
        saveInteractionStatusAssertion.isOk();
        // Deleting the interaction
        webTestClient.delete()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions/{name}",
                        patientInDB_Id, medicineInDB_Id, interactionRequest.getName())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isOk();
        // Verifying that interaction no longer exists
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions/{name}",
                        patientInDB_Id, medicineInDB_Id, interactionRequest.getName())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s".formatted(savePatient_getToken)))
                .exchange()
                .expectStatus().isNotFound();
    }
}