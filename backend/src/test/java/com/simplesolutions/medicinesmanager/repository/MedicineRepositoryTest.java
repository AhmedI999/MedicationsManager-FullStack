package com.simplesolutions.medicinesmanager.repository;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.AbstractTestContainers;
import com.simplesolutions.medicinesmanager.model.Medicine;
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
class MedicineRepositoryTest extends AbstractTestContainers {
    @Autowired
    private PatientRepository patientTest;
    @Autowired
    private MedicineRepository medicineTest;
    private Patient patient;
    private Medicine medicine;
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
                .patientMedicines(Collections.singletonList(medicine))
                .build();

        medicine = Medicine.builder()
                .brandName(faker.lorem().characters(5))
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(faker.lorem().words(4))
                .build();
        medicine.setPatient(patient);
    }
    @Nested
    @DisplayName("For existsMedicineByBrandName method")
    class MedicineRepository_existsMedicineByBrandName{
        @Test
        @DisplayName("Medicine Exists with case valid brand name")
        void existsMedicineByPatientEmailAndBrandName_returnsTrue() {
            // Given
            patientTest.save(patient);
            medicineTest.save(medicine);
            //When
            boolean actual = medicineTest.existsMedicineByPatient_EmailAndBrandName(patient.getEmail(), medicine.getBrandName());
            //Then
            assertThat(actual).isTrue();
        }
        @Test
        @DisplayName("Medicine doesn't Exist with case invalid brand name")
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
    class MedicineRepository_findByPatientIdAndId {
        @Test
        @DisplayName("Medicine exists with case PatientIdAndMedicineId")
        void findByPatientIdAndId_returnsMedicine() {
            // Given
            patientTest.save(patient);
            medicineTest.save(medicine);
            //When
            Optional<Medicine> actual = medicineTest
                    .findByPatientIdAndId(patient.getId(), medicine.getId());
            //Then
            assertThat(actual).isNotNull();
        }
        @Test
        @DisplayName("Medicine doesn't exist with case invalid PatientIdAndMedicineId")
        void findByPatientIdAndId_returnsNull() {
            // Given
            int invalidPatientId = -1;
            int invalidMedicineId = -1;
            //When
            Optional<Medicine> actual = medicineTest.findByPatientIdAndId(invalidPatientId, invalidMedicineId);
            //Then
            assertThat(actual).isNotPresent();
        }
    }
    @Nested
    @DisplayName("For findByPatientIdAndBrandName method")
    class MedicineRepository_findByPatientIdAndBrandName {

        @Test
        @DisplayName("Returns Medicine with patient id and Medicine brand name")
        void findByPatientIdAndBrandName_returnsTrue() {
            // Given
            patientTest.save(patient);
            medicineTest.save(medicine);
            int paitentId = patientTest.findByEmail(patient.getEmail()).getId();
            //When
            Medicine actual = medicineTest.findByPatientIdAndBrandName(paitentId, medicine.getBrandName());
            //Then
            assertThat(actual).isNotNull();
        }

        @Test
        @DisplayName("Throws ResourceNotFoundException with patient id and Invalid Medicine brandName")
        void findByPatientIdAndBrandName_Throw() {
            // Given
            int invalidPatientId = -1;
            String invalidMedicineBrandName = "InvalidBrand";
            //When
            Medicine actual = medicineTest.findByPatientIdAndBrandName(invalidPatientId, invalidMedicineBrandName);
            //Then
            assertThat(actual).isNull();
        }
    }
}