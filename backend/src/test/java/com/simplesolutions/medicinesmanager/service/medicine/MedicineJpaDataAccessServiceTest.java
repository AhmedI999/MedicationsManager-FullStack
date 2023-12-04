package com.simplesolutions.medicinesmanager.service.medicine;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.model.Medicine;
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
class MedicineJpaDataAccessServiceTest {
    MedicineJpaDataAccessService medicineJpaTest;
    Patient patient;
    Medicine medicine;
    @Mock
    MedicineRepository medicineRepository;
    @Mock
    PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        medicineJpaTest = new MedicineJpaDataAccessService(patientRepository, medicineRepository);
        Faker faker = new Faker();
        medicine = Medicine.builder()
                .brandName(faker.lorem().characters(5))
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(faker.lorem().words(4))
                .build();

        patient = Patient.builder()
                .email(faker.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .password(faker.internet().password())
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .age(faker.number().randomDigitNotZero())
                .patientMedicines(Collections.singletonList(medicine))
                .build();
    }

    @Test
    @DisplayName("Verify that selectPatientMedicines() can invoke findById()")
    void selectPatientMedicines() {
        // Given
        int id = 1;
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        //When
        List<Medicine> actual = medicineJpaTest.selectPatientMedicines(id);
        //Then
        verify(patientRepository).findById(id);
        assertThat(actual).isEqualTo(patient.getPatientMedicines());
    }

    @Test
    @DisplayName("Verify that selectPatientMedicineById() can invoke findByPatientIdAndId()")
    void selectPatientMedicineById() {
        // Given
        int patientId = 1;
        int medicineId = 1;
        when(medicineRepository.findByPatientIdAndId(patientId, medicineId))
                .thenReturn(Optional.of(medicine));
        //When
        medicineJpaTest.selectPatientMedicineById(patientId, medicineId);
        //Then
        verify(medicineRepository).findByPatientIdAndId(patientId, medicineId);
    }

    @Test
    @DisplayName("Verify that saveMedicine()  can invoke save()")
    void saveMedicine() {
        //When
        medicineJpaTest.saveMedicine(medicine);
        //Then
        verify(medicineRepository).save(medicine);
    }

    @Test
    @DisplayName("Verify that deletePatientMedicineById() can invoke delete()")
    void deletePatientMedicineById() {
        // Given
        int patientId = 1;
        int medicineId = 1;
        when(medicineRepository.findByPatientIdAndId(patientId, medicineId))
                .thenReturn(Optional.of(medicine));
        //When
        medicineJpaTest.deletePatientMedicineById(patientId, medicineId);
        //Then
        verify(medicineRepository).delete(medicine);
    }

    @Test
    @DisplayName("Verify that doesPatientMedicineExists() can invoke existsMedicineByBrandName()")
    void doesPatientMedicineExists() {
        //When
        medicineJpaTest.doesPatientMedicineExists(patient.getEmail(), medicine.getBrandName());
        //Then
        verify(medicineRepository).existsMedicineByPatient_EmailAndBrandName(patient.getEmail(), medicine.getBrandName());
    }

    @Test
    @DisplayName("Verify that updateMedicine() can invoke save()")
    void updateMedicine() {
        // Given
        String newBrandName = "Nevlop";
        medicine.setBrandName(newBrandName);
        //When
        medicineJpaTest.updateMedicine(medicine);
        //Then
        verify(medicineRepository).save(medicine);
        assertThat(medicine.getBrandName()).isEqualTo(newBrandName);
    }
}