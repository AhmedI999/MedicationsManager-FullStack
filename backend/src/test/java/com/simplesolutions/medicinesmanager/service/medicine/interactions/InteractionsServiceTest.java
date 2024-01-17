package com.simplesolutions.medicinesmanager.service.medicine.interactions;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.InteractionType;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.dto.interactiondto.MedicationInteractionDTO;
import com.simplesolutions.medicinesmanager.service.medicine.MedicineDao;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("Tests for Interactions Service class")
class InteractionsServiceTest {
    @Mock
    MedicineDao medicineDao;
    @Mock
    InteractionDao interactionDao;
    InteractionsService interactionsTest;
    Faker faker;
    // Entities
    @Value("#{'${medicine.picture-url}'}")
    private String DEFAULT_PICTURE_URL;
    Medication medication;
    MedicationInteractions interaction;
    MedicationInteractionDTO interactionRegistrationTest;

    @BeforeEach
    void setUp() {
        interactionsTest = new InteractionsService(medicineDao, interactionDao);
        faker = new Faker();
        interaction = MedicationInteractions.builder()
                .name("U" + faker.lorem().word())
                .Type(InteractionType.MILD)
                .build();
        medication = Medication.builder()
                .pictureUrl(DEFAULT_PICTURE_URL)
                .brandName(faker.lorem().characters(5))
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(Collections.singletonList(interaction))
                .build();
        // Random is used to retrieve random value from InteractionType enum
        Random random = new Random();
        interactionRegistrationTest = new MedicationInteractionDTO(
                "U" + faker.lorem().word(),
                InteractionType.values()[random.nextInt(InteractionType.values().length)]
        );

    }
    @Nested
    @DisplayName("For getMedicineInteractions Method")
    class InteractionsService_getMedicationInteractions {

        @Test
        @DisplayName("verify that getMedicineInteractions() can invoke selectMedicineInteractions dao")
        void getMedicineInteractions_returnInteractions() {
            // Given
            int patientId = 1;
            when(interactionDao.selectMedicineInteractions(patientId, medication.getId()))
                    .thenReturn(Collections.singletonList(interaction));
            //When
            List<MedicationInteractions> actualInteractions = interactionsTest
                    .getMedicineInteractions(patientId, medication.getId());
            //Then
            verify(interactionDao).selectMedicineInteractions(patientId, medication.getId());
            assertThat(actualInteractions).isNotEmpty();
        }

        @Test
        @DisplayName("verify that getMedicineInteractions() return emptyList when Interactions are null or empty")
        void getMedicineInteractions_returnEmptyList() {
            // Given
            int patientId = 1;
            when(interactionDao.selectMedicineInteractions(patientId, medication.getId()))
                    .thenReturn(Collections.emptyList());
            //When
            List<MedicationInteractions> actualInteractions = interactionsTest
                    .getMedicineInteractions(patientId, medication.getId());
            //Then
            verify(interactionDao).selectMedicineInteractions(patientId, medication.getId());
            assertThat(actualInteractions).isEmpty();
        }
    }

    @Nested
    @DisplayName("For getMedicineInteractionByName Method")
    class InteractionsService_getMedicationInteractionByName {

        @Test
        @DisplayName("verify that getMedicineInteractionByName can invoke selectMedicineInteractionByName Dao")
        void getMedicineInteractionByName_returnInteraction() {
            // Given
            when(interactionDao.selectMedicineInteractionByName(medication.getId(), interaction.getName()))
                    .thenReturn(interaction);
            //When
            MedicationInteractions actualInteraction = interactionsTest
                    .getMedicineInteractionByName(medication.getId(), interaction.getName());
            //Then
            verify(interactionDao).selectMedicineInteractionByName(medication.getId(), interaction.getName());
            assertThat(actualInteraction).isNotNull();
            assertThat(actualInteraction).isEqualTo(interaction);
        }

        @Test
        @DisplayName("Verify that getMedicineInteractionByName Throws ResourceNotFound With invalid Interaction")
        void getMedicineInteractionByName_throwResourceNotFound() {
            // Given
            String invalidCapitalizedName = "Nonexistent interaction";
            when(interactionDao.selectMedicineInteractionByName(medication.getId(), invalidCapitalizedName))
                    .thenReturn(null);
            //When
            assertThatThrownBy(() -> interactionsTest
                    .getMedicineInteractionByName(medication.getId(), invalidCapitalizedName))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("This interaction %s doesn't exist".formatted(invalidCapitalizedName));
            //Then
            verify(interactionDao).selectMedicineInteractionByName(medication.getId(), invalidCapitalizedName);
        }
    }

    @Nested
    @DisplayName("For deleteMedicationInteractionByName Method")
    class InteractionsService_deleteMedicationInteractionByName {
        @Test
        @DisplayName("Verify that deleteMedicationInteractionByName can invoke deleteMedicineInteractionByName Dao")
        void deleteMedicationInteractionByName_success() {
            // Given
            when(interactionDao.doesMedicineInteractionExists(medication.getId(), interaction.getName()))
                    .thenReturn(true);
            doNothing().when(interactionDao).deleteMedicineInteractionByName(medication.getId(), interaction.getName());
            //When
            interactionsTest.deleteMedicationInteractionByName(medication.getId(), interaction.getName());
            //Then
            verify(interactionDao).deleteMedicineInteractionByName(medication.getId(), interaction.getName());
        }

        @Test
        @DisplayName("Verify that deleteMedicineInteractionByName Throws ResourceNotFound When name doesn't exist")
        void deleteMedicationInteractionByName_throwResourceNotFound() {
            // Given
            String invalidInteractionName = "Dababy";
            when(interactionDao.doesMedicineInteractionExists(medication.getId(), invalidInteractionName))
                    .thenReturn(false);
            //When
            assertThatThrownBy(() -> interactionsTest
                    .deleteMedicationInteractionByName(medication.getId(), invalidInteractionName))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Medication Interaction %s wasn't found".formatted(invalidInteractionName));
            //Then
            verify(interactionDao, never())
                    .deleteMedicineInteractionByName(medication.getId(), invalidInteractionName);

        }
    }

    @Nested
    @DisplayName("For savePatientMedicine Method")
    class InteractionsService_saveMedicationInteraction {

        @Test
        @DisplayName("Verify that savePatientMedicine Throws DuplicateResource with existing interaction")
        void saveMedicineInteraction_throwDuplicateResourceException() {
            // Given
            when(interactionDao.doesMedicineInteractionExists(medication.getId(), interactionRegistrationTest.name()))
                    .thenReturn(true);
            //When
            assertThatThrownBy(() -> interactionsTest.saveMedicineInteraction(interactionRegistrationTest, medication))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Medication's Interaction (%s) already Exists"
                            .formatted(interactionRegistrationTest.name()));
            //Then
            verify(interactionDao, never()).saveMedicineInteraction(any());
        }

        @Test
        @DisplayName("Verify that savePatientMedicine Throws ResourceNotFound with nonexistent medication")
        void saveMedicineInteraction_throwResourceNotFound() {
            // Given
            when(interactionDao.doesMedicineInteractionExists(medication.getId(), interactionRegistrationTest.name()))
                    .thenReturn(false);
            // email is needed for this exception
            medication.setPatient(Patient.builder().email("patient@example.com").build());
            when(medicineDao.doesPatientMedicineExists(medication.getPatient().getEmail(), medication.getBrandName()))
                    .thenReturn(false);
            //When
            assertThatThrownBy(() -> interactionsTest.saveMedicineInteraction(interactionRegistrationTest, medication))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Medication %s doesn't exist".formatted(medication.getBrandName()));
            //Then
            verify(interactionDao, never()).saveMedicineInteraction(any());
        }

        @Test
        @DisplayName("Verify that savePatientMedicine can invoke saveMedicineInteraction")
        void saveMedicineInteraction_Success() {
            // Given
            when(interactionDao.doesMedicineInteractionExists(medication.getId(), interactionRegistrationTest.name()))
                    .thenReturn(false);
            medication.setPatient(Patient.builder().email("patient@example.com").build());
            when(medicineDao.doesPatientMedicineExists(medication.getPatient().getEmail(), medication.getBrandName()))
                    .thenReturn(true);
            //When
            interactionsTest.saveMedicineInteraction(interactionRegistrationTest, medication);
            ArgumentCaptor<MedicationInteractions> interactionArgumentCaptor = ArgumentCaptor.forClass(MedicationInteractions.class);
            //Then
            verify(interactionDao).saveMedicineInteraction(interactionArgumentCaptor.capture());
            MedicationInteractions interaction = interactionArgumentCaptor.getValue();
            assertThat(interaction.getName()).isEqualTo(interactionRegistrationTest.name());
        }
    }

}