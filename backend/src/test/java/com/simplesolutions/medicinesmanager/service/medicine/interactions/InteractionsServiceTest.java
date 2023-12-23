package com.simplesolutions.medicinesmanager.service.medicine.interactions;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.InteractionType;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.paylod.MedicationInteractionRequest;
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
    Medicine medicine;
    MedicationInteractions interaction;
    MedicationInteractionRequest interactionRegistrationTest;

    @BeforeEach
    void setUp() {
        interactionsTest = new InteractionsService(medicineDao, interactionDao);
        faker = new Faker();
        interaction = MedicationInteractions.builder()
                .name("U" + faker.lorem().word())
                .Type(InteractionType.MILD)
                .build();
        medicine = Medicine.builder()
                .pictureUrl(DEFAULT_PICTURE_URL)
                .brandName(faker.lorem().characters(5))
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(Collections.singletonList(interaction))
                .build();
        // Random is used to retrieve random value from InteractionType enum
        Random random = new Random();
        interactionRegistrationTest = new MedicationInteractionRequest(
                "U" + faker.lorem().word(),
                InteractionType.values()[random.nextInt(InteractionType.values().length)]
        );

    }
    @Nested
    @DisplayName("For getMedicineInteractions Method")
    class InteractionsService_getMedicineInteractions {

        @Test
        @DisplayName("verify that getMedicineInteractions() can invoke selectMedicineInteractions dao")
        void getMedicineInteractions_returnInteractions() {
            // Given
            int patientId = 1;
            when(interactionDao.selectMedicineInteractions(patientId, medicine.getId()))
                    .thenReturn(Collections.singletonList(interaction));
            //When
            List<MedicationInteractions> actualInteractions = interactionsTest
                    .getMedicineInteractions(patientId, medicine.getId());
            //Then
            verify(interactionDao).selectMedicineInteractions(patientId, medicine.getId());
            assertThat(actualInteractions).isNotEmpty();
        }

        @Test
        @DisplayName("verify that getMedicineInteractions() return emptyList when Interactions are null or empty")
        void getMedicineInteractions_returnEmptyList() {
            // Given
            int patientId = 1;
            when(interactionDao.selectMedicineInteractions(patientId, medicine.getId()))
                    .thenReturn(Collections.emptyList());
            //When
            List<MedicationInteractions> actualInteractions = interactionsTest
                    .getMedicineInteractions(patientId, medicine.getId());
            //Then
            verify(interactionDao).selectMedicineInteractions(patientId, medicine.getId());
            assertThat(actualInteractions).isEmpty();
        }
    }

    @Nested
    @DisplayName("For getMedicineInteractionByName Method")
    class InteractionsService_getMedicineInteractionByName {

        @Test
        @DisplayName("verify that getMedicineInteractionByName can invoke selectMedicineInteractionByName Dao")
        void getMedicineInteractionByName_returnInteraction() {
            // Given
            when(interactionDao.selectMedicineInteractionByName(medicine.getId(), interaction.getName()))
                    .thenReturn(interaction);
            //When
            MedicationInteractions actualInteraction = interactionsTest
                    .getMedicineInteractionByName(medicine.getId(), interaction.getName());
            //Then
            verify(interactionDao).selectMedicineInteractionByName(medicine.getId(), interaction.getName());
            assertThat(actualInteraction).isNotNull();
            assertThat(actualInteraction).isEqualTo(interaction);
        }

        @Test
        @DisplayName("Verify that getMedicineInteractionByName Throws ResourceNotFound With invalid Interaction")
        void getMedicineInteractionByName_throwResourceNotFound() {
            // Given
            String invalidCapitalizedName = "Nonexistent interaction";
            when(interactionDao.selectMedicineInteractionByName(medicine.getId(), invalidCapitalizedName))
                    .thenReturn(null);
            //When
            assertThatThrownBy(() -> interactionsTest
                    .getMedicineInteractionByName(medicine.getId(), invalidCapitalizedName))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("This interaction %s doesn't exist".formatted(invalidCapitalizedName));
            //Then
            verify(interactionDao).selectMedicineInteractionByName(medicine.getId(), invalidCapitalizedName);
        }
    }

    @Nested
    @DisplayName("For deleteMedicationInteractionByName Method")
    class InteractionsService_deleteMedicationInteractionByName {
        @Test
        @DisplayName("Verify that deleteMedicationInteractionByName can invoke deleteMedicineInteractionByName Dao")
        void deleteMedicationInteractionByName_success() {
            // Given
            when(interactionDao.doesMedicineInteractionExists(medicine.getId(), interaction.getName()))
                    .thenReturn(true);
            doNothing().when(interactionDao).deleteMedicineInteractionByName(medicine.getId(), interaction.getName());
            //When
            interactionsTest.deleteMedicationInteractionByName(medicine.getId(), interaction.getName());
            //Then
            verify(interactionDao).deleteMedicineInteractionByName(medicine.getId(), interaction.getName());
        }

        @Test
        @DisplayName("Verify that deleteMedicineInteractionByName Throws ResourceNotFound When name doesn't exist")
        void deleteMedicationInteractionByName_throwResourceNotFound() {
            // Given
            String invalidInteractionName = "Dababy";
            when(interactionDao.doesMedicineInteractionExists(medicine.getId(), invalidInteractionName))
                    .thenReturn(false);
            //When
            assertThatThrownBy(() -> interactionsTest
                    .deleteMedicationInteractionByName(medicine.getId(), invalidInteractionName))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Medicine Interaction %s wasn't found".formatted(invalidInteractionName));
            //Then
            verify(interactionDao, never())
                    .deleteMedicineInteractionByName(medicine.getId(), invalidInteractionName);

        }
    }

    @Nested
    @DisplayName("For savePatientMedicine Method")
    class InteractionsService_saveMedicineInteraction {

        @Test
        @DisplayName("Verify that savePatientMedicine Throws DuplicateResource with existing interaction")
        void saveMedicineInteraction_throwDuplicateResourceException() {
            // Given
            when(interactionDao.doesMedicineInteractionExists(medicine.getId(), interactionRegistrationTest.getName()))
                    .thenReturn(true);
            //When
            assertThatThrownBy(() -> interactionsTest.saveMedicineInteraction(interactionRegistrationTest, medicine))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Medicine's Interaction (%s) already Exists"
                            .formatted(interactionRegistrationTest.getName()));
            //Then
            verify(interactionDao, never()).saveMedicineInteraction(any());
        }

        @Test
        @DisplayName("Verify that savePatientMedicine Throws ResourceNotFound with nonexistent medicine")
        void saveMedicineInteraction_throwResourceNotFound() {
            // Given
            when(interactionDao.doesMedicineInteractionExists(medicine.getId(), interactionRegistrationTest.getName()))
                    .thenReturn(false);
            // email is needed for this exception
            medicine.setPatient(Patient.builder().email("patient@example.com").build());
            when(medicineDao.doesPatientMedicineExists(medicine.getPatient().getEmail(), medicine.getBrandName()))
                    .thenReturn(false);
            //When
            assertThatThrownBy(() -> interactionsTest.saveMedicineInteraction(interactionRegistrationTest, medicine))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Medicine %s doesn't exist".formatted(medicine.getBrandName()));
            //Then
            verify(interactionDao, never()).saveMedicineInteraction(any());
        }

        @Test
        @DisplayName("Verify that savePatientMedicine can invoke saveMedicineInteraction")
        void saveMedicineInteraction_Success() {
            // Given
            when(interactionDao.doesMedicineInteractionExists(medicine.getId(), interactionRegistrationTest.getName()))
                    .thenReturn(false);
            medicine.setPatient(Patient.builder().email("patient@example.com").build());
            when(medicineDao.doesPatientMedicineExists(medicine.getPatient().getEmail(), medicine.getBrandName()))
                    .thenReturn(true);
            //When
            interactionsTest.saveMedicineInteraction(interactionRegistrationTest, medicine);
            ArgumentCaptor<MedicationInteractions> interactionArgumentCaptor = ArgumentCaptor.forClass(MedicationInteractions.class);
            //Then
            verify(interactionDao).saveMedicineInteraction(interactionArgumentCaptor.capture());
            MedicationInteractions interaction = interactionArgumentCaptor.getValue();
            assertThat(interaction.getName()).isEqualTo(interactionRegistrationTest.getName());
        }
    }

}