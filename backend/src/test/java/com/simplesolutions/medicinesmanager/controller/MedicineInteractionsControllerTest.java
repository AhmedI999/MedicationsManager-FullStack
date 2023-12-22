package com.simplesolutions.medicinesmanager.controller;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.model.InteractionType;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.paylod.MedicationInteractionRequest;
import com.simplesolutions.medicinesmanager.paylod.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.paylod.PatientRegistrationRequest;
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
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Medicine Interactions controller Integration Tests")
class MedicineInteractionsControllerTest {
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
    MedicationInteractionRequest interactionRequest;
    MedicationInteractions expectedInteraction;
    MedicineRegistrationRequest medicineRequest;
    Medicine expectedMedicine;
    int medicineInDB_Id;
    PatientRegistrationRequest patientRequest;
    Patient expectedPatient;
    int patientInDB_Id;

    // Patient and Medicine Response spec
    WebTestClient.ResponseSpec savePatientStatusAssertion;
    WebTestClient.ResponseSpec saveMedicineStatusAssertion;
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
        expectedPatient = Patient.builder()
                .email(patientRequest.getEmail())
                .password(patientRequest.getPassword())
                .firstname(patientRequest.getFirstname())
                .lastname(patientRequest.getLastname())
                .age(patientRequest.getAge())
                .patientMedicines(new ArrayList<>())
                .build();
        // Interaction
        Random random = new Random();
        interactionRequest = new MedicationInteractionRequest(
                "U" + faker.lorem().word(),
                InteractionType.values()[random.nextInt(InteractionType.values().length)]
        );
        expectedInteraction = MedicationInteractions.builder()
                .name(interactionRequest.getName())
                .Type(interactionRequest.getType())
                .medicine(expectedMedicine)
                .build();
        // Medicine
        medicineRequest = new MedicineRegistrationRequest(
                DEFAULT_PICTURE_URL,
                faker.lorem().word(),
                faker.lorem().characters(10),
                faker.random().nextInt(1, 5),
                faker.lorem().characters());

        expectedMedicine = Medicine.builder()
                .pictureUrl(DEFAULT_PICTURE_URL)
                .brandName(medicineRequest.getBrandName())
                .activeIngredient(medicineRequest.getActiveIngredient())
                .timesDaily(medicineRequest.getTimesDaily())
                .instructions(medicineRequest.getInstructions())
                .interactions(new ArrayList<>())
                .patient(expectedPatient)
                .build();

        // patient Response specs
        savePatientStatusAssertion = webTestClient.post()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(patientRequest), PatientRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        // Retrieving Patient id from Database
        patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();


        // Medicine Response specs
        saveMedicineStatusAssertion = webTestClient.post()
                .uri(path + "/{patientId}/medicines", patientInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(medicineRequest), MedicineRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        // Retrieving Medicine id from Database
        medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, medicineRequest.getBrandName()).getId();

        // Interaction Status assertion
        saveInteractionStatusAssertion = webTestClient.post()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(interactionRequest), MedicationInteractionRequest.class)
                .exchange().expectStatus();

    }

    @Test
    @DisplayName("Ensure that saveMedicationInteraction can save interaction successfully to specific medicine")
    void saveMedicationInteraction() {
        // Saving interaction to the medicine and verifying the result is 200
        saveInteractionStatusAssertion.isOk();
    }

    @Test
    @DisplayName("Ensure that getMedicationInteraction endPoint can retrieve interaction by name")
    void getMedicationInteraction() {
        // saving interaction
        saveInteractionStatusAssertion.isOk();
        // Retrieving Patient, Medicine, and Interaction id from Database
        int patientInDB_Id = patientRepository.findByEmail(patientRequest.getEmail()).getId();
        int medicineInDB_Id = medicineRepository
                .findByPatientIdAndBrandName(patientInDB_Id, medicineRequest.getBrandName()).getId();
        // Retrieving the interaction
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions/{name}",
                        patientInDB_Id, medicineInDB_Id, interactionRequest.getName())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<MedicationInteractions>() {
                })
                .consumeWith(response -> {
                    MedicationInteractions actualInteraction = response.getResponseBody();
                    assertThat(actualInteraction)
                            .usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(expectedInteraction);
                });
    }

    @Test
    @DisplayName("Ensure that getAllMedicationInteractions endPoint can retrieve interactions ")
    void getAllMedicationInteractions() {
        // Saving Interaction for medicine
        saveInteractionStatusAssertion.isOk();
        // Retrieving List of interactions
        List<MedicationInteractions> allMedicationInteractions = webTestClient.get()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions", patientInDB_Id, medicineInDB_Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<MedicationInteractions>() {
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
        // Saving Interaction for medicine
        saveInteractionStatusAssertion.isOk();
        // Deleting the interaction
        webTestClient.delete()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions/{name}",
                        patientInDB_Id, medicineInDB_Id, interactionRequest.getName())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
        // Verifying that interaction no longer exists
        webTestClient.get()
                .uri(path + "/{patientId}/medicines/{medicineId}/interactions/{name}",
                        patientInDB_Id, medicineInDB_Id, interactionRequest.getName())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}