package com.simplesolutions.medicinesmanager.repository;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.AbstractTestContainers;
import com.simplesolutions.medicinesmanager.model.InteractionType;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
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

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("Test For Custom MedicineRepository methods")
class MedicationRepositoryTest extends AbstractTestContainers {
    @Autowired
    PatientRepository patientTest;
    @Autowired
    MedicineRepository medicineTest;
    Patient patient;
    Medication medication;
    Faker faker;

    @BeforeEach
    void setUp() {
        medicineTest.deleteAll();
        faker = new Faker();
        patient = Patient.builder()
                .email(faker.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .password(faker.internet().password())
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .age(faker.number().randomDigitNotZero())
                .patientMedications(Collections.singletonList(medication))
                .build();

        MedicationInteractions interactions = MedicationInteractions.builder()
                .name(faker.lorem().word())
                .Type(InteractionType.MILD)
                .build();
        medication = Medication.builder()
                .brandName("U" + faker.lorem().word())
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(Collections.singletonList(interactions))
                .build();
        medication.setPatient(patient);
    }
    @Nested
    @DisplayName("For existsMedicineByBrandName method")
    class MedicineRepository_existsMedicationByBrandName {
        @Test
        @DisplayName("Medication Exists with case valid brand name")
        void existsMedicineByPatientEmailAndBrandName_returnsTrue() {
            // Given
            patientTest.save(patient);
            medicineTest.save(medication);
            //When
            boolean actual = medicineTest.existsMedicineByPatient_EmailAndBrandName(patient.getEmail(), medication.getBrandName());
            //Then
            assertThat(actual).isTrue();
        }
        @Test
        @DisplayName("Medication doesn't Exist with case invalid brand name")
        void existsMedicineByPatientEmailAndBrandName_returnsFalse() {
            // Given
            String invalidBrandName = faker.lorem().characters(10);
            //When
            boolean actual = medicineTest.existsMedicineByPatient_EmailAndBrandName(patient.getEmail(), invalidBrandName);
            //Then
            assertThat(actual).isFalse();
        }
    }


    @Nested
    @DisplayName("For findByPatientIdAndId method")
    class MedicationRepository_findByPatientIdAndId {
        @Test
        @DisplayName("Medication exists with case PatientIdAndMedicineId")
        void findByPatientIdAndId_returnsMedicine() {
            // Given
            patientTest.save(patient);
            medicineTest.save(medication);
            //When
            Optional<Medication> actual = medicineTest
                    .findByPatientIdAndId(patient.getId(), medication.getId());
            //Then
            assertThat(actual).isNotNull();
        }
        @Test
        @DisplayName("Medication doesn't exist with case invalid PatientIdAndMedicineId")
        void findByPatientIdAndId_returnsNull() {
            // Given
            int invalidPatientId = -1;
            int invalidMedicineId = -1;
            //When
            Optional<Medication> actual = medicineTest.findByPatientIdAndId(invalidPatientId, invalidMedicineId);
            //Then
            assertThat(actual).isNotPresent();
        }
    }
    @Nested
    @DisplayName("For findByPatientIdAndBrandName method")
    class MedicationRepository_findByPatientIdAndBrandName {

        @Test
        @DisplayName("Returns Medication with patient id and Medication brand name")
        void findByPatientIdAndBrandName_returnsMedication() {
            // Given
            patientTest.save(patient);
            medicineTest.save(medication);
            int paitentId = patientTest.findByEmail(patient.getEmail()).orElseThrow().getId();
            //When
            Optional<Medication> actual = medicineTest.findByPatientIdAndBrandName(paitentId, medication.getBrandName());
            //Then
            assertThat(actual).isPresent();
        }

        @Test
        @DisplayName("Throws ResourceNotFoundException with patient id and Invalid Medication brandName")
        void findByPatientIdAndBrandName_Throw() {
            // Given
            int invalidPatientId = -1;
            String invalidMedicineBrandName = "InvalidBrand";
            //When
            Optional<Medication> actual = medicineTest.findByPatientIdAndBrandName(invalidPatientId, invalidMedicineBrandName);
            //Then
            assertThat(actual).isNotPresent();
        }
    }
}