package com.simplesolutions.medicinesmanager.service.medicine;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.model.InteractionType;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.repository.MedicineRepository;
import com.simplesolutions.medicinesmanager.repository.PatientRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("For MedicineJpaDataAccessService Class")
class MedicationJpaDataAccessServiceTest {
    MedicineJpaDataAccessService medicineJpaTest;
    Patient patient;
    Medication medication;
    MedicationInteractions interactions;
    @Mock
    MedicineRepository medicineRepository;
    @Mock
    PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        medicineJpaTest = new MedicineJpaDataAccessService(patientRepository, medicineRepository);
        Faker faker = new Faker();
        interactions = MedicationInteractions.builder()
                .name(faker.lorem().word())
                .Type(InteractionType.MILD)
                .build();
        medication = Medication.builder()
                .brandName(faker.lorem().characters(5))
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(Collections.singletonList(interactions))
                .build();

        patient = Patient.builder()
                .email(faker.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .password(faker.internet().password())
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .age(faker.number().randomDigitNotZero())
                .patientMedications(Collections.singletonList(medication))
                .build();
    }

    @Test
    @DisplayName("Verify that selectPatientMedicines() can invoke findById()")
    void selectPatientMedicines() {
        // Given
        int id = 1;
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        //When
        List<Medication> actual = medicineJpaTest.selectPatientMedicines(id);
        //Then
        verify(patientRepository).findById(id);
        assertThat(actual).isEqualTo(patient.getPatientMedications());
    }

    @Test
    @DisplayName("Verify that selectPatientMedicineById() can invoke findByPatientIdAndId()")
    void selectPatientMedicineById() {
        // Given
        int patientId = 1;
        int medicineId = 1;
        when(medicineRepository.findByPatientIdAndId(patientId, medicineId))
                .thenReturn(Optional.of(medication));
        //When
        medicineJpaTest.selectPatientMedicineById(patientId, medicineId);
        //Then
        verify(medicineRepository).findByPatientIdAndId(patientId, medicineId);
    }
    @Test
    @DisplayName("Verify that selectPatientMedicineByBrandName() can invoke findByPatientIdAndBrandName()")
    void selectPatientMedicineByBrandName() {
        // Given
        int patientId = 1;
        when(medicineRepository.findByPatientIdAndBrandName(patientId, medication.getBrandName()))
                .thenReturn(Optional.of(medication));
        //When
        medicineJpaTest.selectPatientMedicineByBrandName(patientId, medication.getBrandName());
        //Then
        verify(medicineRepository).findByPatientIdAndBrandName(patientId, medication.getBrandName());
    }



    @Test
    @DisplayName("Verify that saveMedicine()  can invoke save()")
    void saveMedicine() {
        //When
        medicineJpaTest.saveMedicine(medication);
        //Then
        verify(medicineRepository).save(medication);
    }

    @Test
    @DisplayName("Verify that deletePatientMedicineById() can invoke delete()")
    void deletePatientMedicineById() {
        // Given
        int patientId = 1;
        int medicineId = 1;
        when(medicineRepository.findByPatientIdAndId(patientId, medicineId))
                .thenReturn(Optional.of(medication));
        //When
        medicineJpaTest.deletePatientMedicineById(patientId, medicineId);
        //Then
        verify(medicineRepository).delete(medication);
    }

    @Test
    @DisplayName("Verify that doesPatientMedicineExists() can invoke existsMedicineByBrandName()")
    void doesPatientMedicineExists() {
        //When
        medicineJpaTest.doesPatientMedicineExists(patient.getEmail(), medication.getBrandName());
        //Then
        verify(medicineRepository).existsMedicineByPatient_EmailAndBrandName(patient.getEmail(), medication.getBrandName());
    }

    @Test
    @DisplayName("Verify that updateMedicine() can invoke save()")
    void updateMedicine() {
        // Given
        String newBrandName = "Nevlop";
        medication.setBrandName(newBrandName);
        //When
        medicineJpaTest.updateMedicine(medication);
        //Then
        verify(medicineRepository).save(medication);
        assertThat(medication.getBrandName()).isEqualTo(newBrandName);
    }
}