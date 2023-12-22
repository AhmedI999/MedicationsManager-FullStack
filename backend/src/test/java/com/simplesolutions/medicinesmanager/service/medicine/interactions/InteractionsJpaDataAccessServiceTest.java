package com.simplesolutions.medicinesmanager.service.medicine.interactions;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.InteractionType;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.repository.MedicationInteractionRepository;
import com.simplesolutions.medicinesmanager.repository.MedicineRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("For InteractionsJpaDataAccessService Class")
class InteractionsJpaDataAccessServiceTest {
    InteractionsJpaDataAccessService interactionJpaTest;
    Medicine medicine;
    MedicationInteractions interaction;
    @Mock
    MedicineRepository medicineRepository;
    @Mock
    MedicationInteractionRepository interactionRepository;
    @Value("#{'${medicine.picture-url}'}")
    String DEFAULT_PICTURE_URL;

    @BeforeEach
    void setUp() {
        interactionJpaTest = new InteractionsJpaDataAccessService(medicineRepository, interactionRepository);
        Faker faker = new Faker();
        interaction = MedicationInteractions.builder()
                .name("U" + faker.lorem().word())
                .Type(InteractionType.Mild)
                .medicine(medicine)
                .build();
        medicine = Medicine.builder()
                .pictureUrl(DEFAULT_PICTURE_URL)
                .brandName(faker.lorem().characters(5))
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(Collections.singletonList(interaction))
                .build();
        interaction.setMedicine(medicine);

    }

    @Test
    @DisplayName("Verify that selectMedicineInteractionByName() can invoke findByMedicineIdAndName()")
    void selectMedicineInteractionByName() {
        // Given
        String interactionName = "Prednisone";
        when(interactionRepository.findByMedicineIdAndName(medicine.getId(), interactionName))
                .thenReturn(interaction);
        //When
        MedicationInteractions actualInteraction = interactionJpaTest
                .selectMedicineInteractionByName(medicine.getId(), interactionName);
        //Then
        verify(interactionRepository).findByMedicineIdAndName(medicine.getId(), interactionName);
        assertThat(actualInteraction)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(interaction);
    }
    @Nested
    @DisplayName("For selectMedicineInteractions method")
    class InteractionsDataAccessService_selectMedicineInteractions {

        @Test
        @DisplayName("Verify that selectMedicineInteractions() can invoke findByPatientIdAndId()")
        void selectMedicineInteractions_returnListOfInteractions() {
            // Given
            int patientId = 1;
            when(medicineRepository.findByPatientIdAndId(patientId, medicine.getId()))
                    .thenReturn(Optional.of(medicine));
            //When
            List<MedicationInteractions> actualInteractionsList = interactionJpaTest
                    .selectMedicineInteractions(patientId, medicine.getId());
            //Then
            verify(medicineRepository).findByPatientIdAndId(patientId, medicine.getId());
            assertThat(actualInteractionsList).isEqualTo(medicine.getInteractions());
        }

        @Test
        @DisplayName("Verify that selectMedicineInteractions() throws Resource not found with invalid medicine")
        void selectMedicineInteractions_throwResourceNotFound() {
            // Given
            int patientId = 1;
            int invalidMedicineId = -1;
            when(medicineRepository.findByPatientIdAndId(patientId, invalidMedicineId))
                    .thenReturn(Optional.empty());
            //When
            assertThatThrownBy(() -> interactionJpaTest
                    .selectMedicineInteractions(patientId, invalidMedicineId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(" Medicine doesn't exist");
            //Then
            verify(medicineRepository).findByPatientIdAndId(patientId, invalidMedicineId);
        }
    }

    @Test
    @DisplayName("verify that saveMedicineInteraction() can invoke save()")
    void saveMedicineInteraction() {
        //When
        interactionJpaTest.saveMedicineInteraction(interaction);
        //Then
        verify(interactionRepository).save(interaction);
    }

    @Test
    void deleteMedicineInteractionByName() {
        // Given
        String interactionName = "Nebivolol";
        when(interactionRepository.findByMedicineIdAndName(medicine.getId(), interactionName))
                .thenReturn(interaction);
        //When
        interactionJpaTest.deleteMedicineInteractionByName(medicine.getId(), interactionName);
        //Then
        verify(interactionRepository).delete(interaction);
    }

    @Test
    void doesMedicineInteractionExists() {
        //When
        interactionJpaTest.doesMedicineInteractionExists(medicine.getId(), interaction.getName());
        //Then
        verify(interactionRepository).existsByMedicineIdAndName(medicine.getId(), interaction.getName());
    }
}