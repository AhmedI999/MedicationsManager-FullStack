package com.simplesolutions.medicinesmanager.service.medicine;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicationDTOMapper;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicationResponseDTO;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.model.InteractionType;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.service.patient.PatientDao;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("Tests for Medication Service class")
class MedicationServiceTest {
    @Mock
    MedicineDao medicineDao;
    @Mock
    PatientDao patientDao;
    MedicineService medicineTest;
    MedicationDTOMapper medicationDTOMapper;
    Faker faker;
    Patient patient;
    Medication medication;
    MedicationResponseDTO expected;
    // for medication registration and the validation
    @Value("#{'${medicine.picture-url}'}")
    String DEFAULT_PICTURE_URL;
    MedicineRegistrationRequest medicineRegistrationTest;
    LocalValidatorFactoryBean validatorFactory;

    @BeforeEach
    void setUp() {
        medicationDTOMapper = new MedicationDTOMapper();
        medicineTest = new MedicineService(patientDao, medicineDao, medicationDTOMapper);

        faker = new Faker();
        MedicationInteractions interactions = MedicationInteractions.builder()
                .name(faker.lorem().word())
                .Type(InteractionType.MILD)
                .build();
        medication = Medication.builder()
                .pictureUrl("https://i.imgur.com/qMA0qhd.png")
                .brandName("U" + faker.lorem().word())
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(Collections.singletonList(interactions))
                .patient(patient)
                .build();
        expected = medicationDTOMapper.apply(medication);

        patient = Patient.builder()
                .id(1)
                .email(faker.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .password(faker.internet().password())
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .age(faker.number().randomDigitNotZero())
                .patientMedications(Collections.singletonList(medication))
                .build();

        medicineRegistrationTest = createMedicineRegistrationRequest(faker.lorem().word());
        validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();
    }
    private MedicineRegistrationRequest createMedicineRegistrationRequest(String brandName){
        return new MedicineRegistrationRequest(
                DEFAULT_PICTURE_URL,
                brandName,
                faker.lorem().word(),
                faker.number().numberBetween(2, 99),
                faker.lorem().word());
    }
    @AfterEach
    void tearDown() {
        patientDao.deletePatientById(patient.getId());
    }
    @Nested
    @DisplayName("getPatientMedications test units")
    class MedicationService_getPatientMedicines {

        @Test
        @DisplayName("Verify that getPatientMedications can invoke selectPatientMedicines()")
        void getPatientMedicines_returnMedicines() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicines(patient.getId())).thenReturn(Collections.singletonList(medication));
            //When
            List<MedicationResponseDTO> actualMedications = medicineTest.getPatientMedicines(patient.getId());
            //Then
            verify(medicineDao).selectPatientMedicines(patient.getId());
            assertThat(actualMedications).isNotEmpty();
        }

        @Test
        @DisplayName("Verify that getPatientMedications Throw ResourceNotFound when patient not found")
        void getPatientMedicines_throwResourceNotFoundPatient() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.empty());
            //When
            assertThatThrownBy(() -> medicineTest.getPatientMedicines(patient.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Patient doesn't exist");
            //Then
            verify(medicineDao, never()).selectPatientMedicines(any());
        }

        @Test
        @DisplayName("Verify that getPatientMedications Throw ResourceNotFoundException when medicines are null")
        void getPatientMedicines_throwResourceNotFoundMedicine() {
            // Given
            patient.setPatientMedications(null);
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));

            //When
            assertThatThrownBy(() -> medicineTest.getPatientMedicines(patient.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Patient doesn't have medications");
            //Then
            assertThat(patient.getPatientMedications()).isNull();
        }
    }

    @Nested
    @DisplayName("getPatientMedicineById test units")
    class MedicineService_getPatientMedicationById {
        @Test
        @DisplayName("Verify that getPatientMedicineById can invoke selectPatientMedicineById")
        void getPatientMedicineById_returnMedicine() {
            // Given
            int patientId = 1;
            int medicineId = 1;
            when(medicineDao.selectPatientMedicineById(patientId, medicineId))
                    .thenReturn(Optional.of(medication));
            //When
            MedicationResponseDTO actual = medicineTest.getPatientMedicineById(patientId, medicineId);
            //Then
            verify(medicineDao).selectPatientMedicineById(patientId, medicineId);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Verify that getPatientMedicineById throws ResourceNotFoundException patient")
        void getPatientMedicineById_throwResourceNotFoundException() {
            // Given
            int patientId = 1;
            int invalidMedicineId = -1;
            when(medicineDao.selectPatientMedicineById(patientId, invalidMedicineId))
                    .thenReturn(Optional.empty());
            //When
            assertThatThrownBy(() -> medicineTest.getPatientMedicineById(patientId, invalidMedicineId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Medication %s wasn't found".formatted(invalidMedicineId));
            //Then
           verify(medicineDao).selectPatientMedicineById(patientId, invalidMedicineId);
        }
    }
    @Nested
    @DisplayName("getPatientMedicineEntityById test units")
    class MedicineService_getPatientMedicineEntityById {
        @Test
        @DisplayName("Verify that getPatientMedicineEntityById can invoke selectPatientMedicineById")
        void getPatientMedicineEntityById_returnMedicine() {
            // Given
            int patientId = 1;
            int medicineId = 1;
            when(medicineDao.selectPatientMedicineById(patientId, medicineId))
                    .thenReturn(Optional.of(medication));
            //When
            Medication actual = medicineTest.getPatientMedicineEntityById(patientId, medicineId);
            //Then
            verify(medicineDao).selectPatientMedicineById(patientId, medicineId);
            assertThat(actual).isEqualTo(medication);
        }

        @Test
        @DisplayName("Verify that getPatientMedicineById throws ResourceNotFoundException patient")
        void getPatientMedicineEntityById_throwResourceNotFoundException() {
            // Given
            int patientId = 1;
            int invalidMedicineId = -1;
            when(medicineDao.selectPatientMedicineById(patientId, invalidMedicineId))
                    .thenReturn(Optional.empty());
            //When
            assertThatThrownBy(() -> medicineTest.getPatientMedicineEntityById(patientId, invalidMedicineId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Medication %s wasn't found".formatted(invalidMedicineId));
            //Then
            verify(medicineDao).selectPatientMedicineById(patientId, invalidMedicineId);
        }
    }
    @Nested
    @DisplayName("getPatientMedicineByBrandName Unit tests")
    class MedicineService_getPatientMedicationByBrandName {
        @Test
        @DisplayName("Verify that getPatientMedicineById can invoke selectPatientMedicineById")
        void getPatientMedicineByBrandName_returnMedicine() {
            // Given
            int patientId = 1;
            when(medicineDao.selectPatientMedicineByBrandName(patientId, medication.getBrandName()))
                    .thenReturn(Optional.of(medication));
            //When
            MedicationResponseDTO actual = medicineTest.getPatientMedicineByBrandName(patientId, medication.getBrandName());
            //Then
            verify(medicineDao).selectPatientMedicineByBrandName(patientId, medication.getBrandName());
            assertThat(actual).isEqualTo(expected);
        }
        @Test
        @DisplayName("Verify that getPatientMedicineById throw  ResourceNotFoundException with invalid medication")
        void getPatientMedicineByBrandName_throwResourceNotFoundException() {
            // Given
            int patientId = 1;
            when(medicineDao.selectPatientMedicineByBrandName(patientId, medication.getBrandName()))
                    .thenReturn(Optional.empty());
            //When
            assertThatThrownBy( () -> medicineTest.getPatientMedicineByBrandName(patientId, medication.getBrandName()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Medication %s wasn't found".formatted(medication.getBrandName()));
            //Then
            verify(medicineDao).selectPatientMedicineByBrandName(patientId, medication.getBrandName());
        }

    }
    @Nested
    @DisplayName("deletePatientMedicineById test units")
    class MedicineService_deletePatientMedicationById {
        @Test
        @DisplayName("Verify that deletePatientMedicineById can invoke delete() repository")
        void deletePatientMedicineById_Success() {
            // Given
            int patientId = 1;
            int medicineId = 1;
            doNothing().when(medicineDao).deletePatientMedicineById(patientId, medicineId);
            // When
            medicineTest.deletePatientMedicineById(patientId, medicineId);
            // Then
            verify(medicineDao).deletePatientMedicineById(patientId, medicineId);
        }

        @Test
        @DisplayName("Verify that deletePatientMedicineById throws ResourceNotFoundException")
        void deletePatientMedicineById_throwResourceNotFoundException() {
            // Given
            int patientId = 1;
            int invalidMedicineId = -1;
            doThrow(new ResourceNotFoundException("Couldn't find medication"))
                    .when(medicineDao).deletePatientMedicineById(patientId, invalidMedicineId);
            //When
            assertThatThrownBy(() -> medicineTest.deletePatientMedicineById(patientId, invalidMedicineId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Couldn't find medication");
            //Then
            verify(medicineDao).deletePatientMedicineById(patientId, invalidMedicineId);
        }
    }

    @Test
    @DisplayName("Verify that doesMedicineExists can invoke doesPatientMedicineExists dao")
    void doesMedicineExists() {
        // Given
        String validBrandName = "Nevelob";
        when(medicineDao.doesPatientMedicineExists(patient.getEmail(), validBrandName)).thenReturn(true);
        //When
        medicineTest.doesMedicineExists(patient.getEmail(), validBrandName);
        //Then
        verify(medicineDao).doesPatientMedicineExists(patient.getEmail(), validBrandName);
    }

    @Nested
    @DisplayName("savePatientMedicine unit tests")
    class MedicineService_savePatientMedication {
        @Test
        @DisplayName("Verify that savePatientMedicine can invoke saveMedicine dao")
        void savePatientMedicine_Success() {
            //Given
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.brandName()))
                    .thenReturn(false);
            when(patientDao.doesPatientExists(patient.getEmail())).thenReturn(true);
            //When
            medicineTest.savePatientMedicine(medicineRegistrationTest, patient);
            //Then
            verify(medicineDao).saveMedicine(any(Medication.class));
            verify(medicineDao).doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.brandName());
            verify(patientDao).doesPatientExists(patient.getEmail());
        }
        @Test
        @DisplayName("Verify that savePatientMedicine can invoke saveMedicine dao with undefined Picture Url")
        void savePatientMedicine_UndefinedUrl_Success() {
            //Given
            MedicineRegistrationRequest undefinedUrlMedicine = new MedicineRegistrationRequest(
                    null,
                    "brandWithNoPicUrl",
                    faker.lorem().word(),
                    faker.number().numberBetween(2, 99),
                    faker.lorem().word()
            );

            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), undefinedUrlMedicine.brandName()))
                    .thenReturn(false);
            when(patientDao.doesPatientExists(patient.getEmail())).thenReturn(true);
            //When
            medicineTest.savePatientMedicine(undefinedUrlMedicine, patient);
            ArgumentCaptor<Medication> medicineArgumentCaptor = ArgumentCaptor.forClass(Medication.class);
            //Then
            verify(medicineDao).saveMedicine(medicineArgumentCaptor.capture());
            verify(medicineDao).doesPatientMedicineExists(patient.getEmail(), undefinedUrlMedicine.brandName());
            Medication capturedMedication = medicineArgumentCaptor.getValue();
            assertThat(capturedMedication.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);

        }

        @Test
        @DisplayName("Verify that savePatientMedicine throw DuplicateResourceException")
        void savePatientMedicine_throwDuplicateResourceException() {
            //Given
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.brandName()))
                    .thenReturn(true);
            //When
            assertThatThrownBy(() -> medicineTest.savePatientMedicine(medicineRegistrationTest, patient))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Patient's medication (%s) already Exists".formatted(medicineRegistrationTest.brandName()));
            //Then
            verify(medicineDao, never()).saveMedicine(any());
            verify(medicineDao).doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.brandName());
        }

        @Test
        @DisplayName("Verify that savePatientMedicine throw ResourceNotFoundException")
        void savePatientMedicine_throwResourceNotFoundException() {
            //Given
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.brandName()))
                    .thenReturn(false);
            when(patientDao.doesPatientExists(patient.getEmail()))
                    .thenReturn(false);
            //When
            assertThatThrownBy(() -> medicineTest.savePatientMedicine(medicineRegistrationTest, patient))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Patient doesn't exist");
            //Then
            verify(medicineDao, never()).saveMedicine(any());
            verify(patientDao).doesPatientExists(patient.getEmail());
        }

        @Test
        @DisplayName("Verify that savePatientMedicine throws RegistrationConstraintsException")
        void savePatientMedicine_throwRegistrationConstraintsException() {
            //Given
            //giving empty brand name to violate the constraints
            MedicineRegistrationRequest emptyBrandMedicine = createMedicineRegistrationRequest("");
            //When
            Set<ConstraintViolation<MedicineRegistrationRequest>> violations =
                    validatorFactory.validate(emptyBrandMedicine);
            //Then
            verify(medicineDao, never()).saveMedicine(any());
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("Brand name is required");
        }
    }


    @Nested
    @DisplayName("editMedicineDetails unit tests")
    class MedicineService_editMedicationDetails {

        @Test
        @DisplayName("Verify that savePatientMedicine can invoke updateMedicine for valid validBrandName")
        void editMedicineDetails_validBrandNameUpdated() {
            // Given
            int patientId = 1;
            int medicineId = 1;
            when(patientDao.selectPatientById(patientId)).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patientId, medicineId))
                    .thenReturn(Optional.of(medication));
            String newValidBrandName = "Nevelob";
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), newValidBrandName))
                    .thenReturn(false);
            //When
            medicineTest.editMedicineDetails(patientId, medicineId,
                    MedicineUpdateRequest.builder().brandName(newValidBrandName).build());

            //Then
            ArgumentCaptor<Medication> medicineArgumentCaptor = ArgumentCaptor.forClass(Medication.class);
            verify(medicineDao).updateMedicine(medicineArgumentCaptor.capture());
            Medication capturedMedication = medicineArgumentCaptor.getValue();
            assertThat(capturedMedication.getBrandName()).isEqualTo(medication.getBrandName());
        }


        @Test
        @DisplayName("Verify that editMedicineDetails Throw DuplicateResourceException")
        void editMedicineDetails_duplicateBrandName() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medication.getId()))
                    .thenReturn(Optional.of(medication));
            String duplicateBrandName = "duplicateBrand";
            // since you want an existing medication vs another medication with same brand name
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), duplicateBrandName)).thenReturn(true);
            //When
            assertThatThrownBy(() ->
                    medicineTest.editMedicineDetails(patient.getId(), medication.getId(),
                            MedicineUpdateRequest.builder().brandName(duplicateBrandName).build()))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Brand name already taken");
            //Then
            verify(medicineDao, never()).updateMedicine(any());
        }

        @Test
        @DisplayName("Verify that editPatientDetails can invoke updateMedicine for validActiveIngredient")
        void editMedicineDetails_validActiveIngredientUpdated() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medication.getId()))
                    .thenReturn(Optional.of(medication));
            String newActiveIngredient = "pseudophenrine";
            //When
            medicineTest.editMedicineDetails(patient.getId(), medication.getId(),
                    MedicineUpdateRequest.builder().activeIngredient(newActiveIngredient).build());
            //Then
            ArgumentCaptor<Medication> MedicineArgumentCaptor = ArgumentCaptor.forClass(Medication.class);
            // ensure that medicineDao has been called for the updated(captured) Medication object
            verify(medicineDao).updateMedicine(MedicineArgumentCaptor.capture());
            Medication capturedMedication = MedicineArgumentCaptor.getValue();
            // Ensure that the Medication's Active Ingredient has been updated
            assertThat(capturedMedication.getActiveIngredient()).isEqualTo(medication.getActiveIngredient());
        }

        @Test
        @DisplayName("Verify that editMedicineDetails can invoke updateMedicine for validTimesDaily")
        void editMedicineDetails_validTimesDailyUpdated() {
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medication.getId()))
                    .thenReturn(Optional.of(medication));
            int newTimesDaily = -1;
            //When
            medicineTest.editMedicineDetails(patient.getId(), medication.getId(),
                    MedicineUpdateRequest.builder().timesDaily(newTimesDaily).build());
            //Then
            ArgumentCaptor<Medication> MedicineArgumentCaptor = ArgumentCaptor.forClass(Medication.class);
            // ensure that medicineDao has been called for the updated(captured) Medication object
            verify(medicineDao).updateMedicine(MedicineArgumentCaptor.capture());
            Medication capturedMedication = MedicineArgumentCaptor.getValue();
            // Ensure that the Medication's Times Daily has been updated
            assertThat(capturedMedication.getTimesDaily()).isEqualTo(medication.getTimesDaily());
        }
        @Test
        @DisplayName("Verify that editMedicineDetails can invoke updateMedicine for valid Instructions")
        void editMedicineDetails_validInstructionsUpdated() {
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medication.getId()))
                    .thenReturn(Optional.of(medication));
            String validInstructions = "interacts with cold medicines";
            //When
            medicineTest.editMedicineDetails(patient.getId(), medication.getId(),
                    MedicineUpdateRequest.builder().instructions(validInstructions).build());
            //Then
            ArgumentCaptor<Medication> MedicineArgumentCaptor = ArgumentCaptor.forClass(Medication.class);
            // ensure that patientDao has been called for the updated(captured) Patient object
            verify(medicineDao).updateMedicine(MedicineArgumentCaptor.capture());
            Medication capturedMedication = MedicineArgumentCaptor.getValue();
            // Ensure that the patient's email has been updated I.E captured and patient in db emails are the same
            assertThat(capturedMedication.getInstructions()).isEqualTo(medication.getInstructions());
        }
        @Test
        @DisplayName("Verify that editMedicineDetails can invoke updateMedicine for picture_url")
        void editMedicineDetails_pictureUrlUpdated() {
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medication.getId()))
                    .thenReturn(Optional.of(medication));
            String pictureUrl = "https://i.imgur.com/qmgE3uS.jpeg";
            //When
            medicineTest.editMedicineDetails(patient.getId(), medication.getId(),
                    MedicineUpdateRequest.builder().pictureUrl(pictureUrl).build());
            //Then
            ArgumentCaptor<Medication> MedicineArgumentCaptor = ArgumentCaptor.forClass(Medication.class);
            // ensure that patientDao has been called for the updated(captured) Patient object
            verify(medicineDao).updateMedicine(MedicineArgumentCaptor.capture());
            Medication capturedMedication = MedicineArgumentCaptor.getValue();
            // Ensure that the patient's email has been updated I.E captured and patient in db emails are the same
            assertThat(capturedMedication.getPictureUrl()).isEqualTo(medication.getPictureUrl());
        }

        @Test
        @DisplayName("Verify that editMedicineDetails Throw UpdateException")
        void editMedicineDetails_noChangesBrandName() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medication.getId()))
                    .thenReturn(Optional.of(medication));
            String sameBrandName = medication.getBrandName();
            //When
            assertThatThrownBy(() -> medicineTest.editMedicineDetails(patient.getId(), medication.getId(),
                    MedicineUpdateRequest.builder().brandName(sameBrandName).build()))
                    .isInstanceOf(UpdateException.class)
                    .hasMessage("no data changes found");
            //Then
            verify(medicineDao, never()).updateMedicine(any());
        }


    }
}