package com.simplesolutions.medicinesmanager.service.medicine;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.paylod.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.paylod.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.service.patient.PatientDao;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("Tests for Medicine Service class")
class MedicineServiceTest {
    @Mock
    MedicineDao medicineDao;
    @Mock
    PatientDao patientDao;
    MedicineService medicineTest;
    Faker faker;
    Patient patient;
    Medicine medicine;
    // for medicine registration and the validation
    MedicineRegistrationRequest medicineRegistrationTest;
    LocalValidatorFactoryBean validatorFactory;

    @BeforeEach
    void setUp() {
        medicineTest = new MedicineService(patientDao, medicineDao);
        faker = new Faker();
        medicine = Medicine.builder()
                .brandName(faker.lorem().characters(5))
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(faker.lorem().words(4))
                .build();

        patient = Patient.builder()
                .id(1)
                .email(faker.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .password(faker.internet().password())
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .age(faker.number().randomDigitNotZero())
                .patientMedicines(Collections.singletonList(medicine))
                .build();

        medicineRegistrationTest = createMedicineRegistrationRequest(faker.lorem().word());
        validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();
    }
    private MedicineRegistrationRequest createMedicineRegistrationRequest(String email){
        return new MedicineRegistrationRequest(
                email,
                faker.lorem().word(),
                faker.number().numberBetween(2,99),
                faker.lorem().word(),
                Arrays.asList(faker.lorem().word(), faker.lorem().word())
        );
    }
    @AfterEach
    void tearDown() {
        patientDao.deletePatientById(patient.getId());
    }
    @Nested
    @DisplayName("getPatientMedicines test units")
    class MedicineService_getPatientMedicines {

        @Test
        @DisplayName("Verify that getPatientMedicines can invoke selectPatientMedicines()")
        void getPatientMedicines_returnMedicines() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            //When
            medicineTest.getPatientMedicines(patient.getId());
            //Then
            verify(medicineDao).selectPatientMedicines(patient.getId());
            assertThat(patient.getPatientMedicines()).isNotEmpty();
        }

        @Test
        @DisplayName("Verify that getPatientMedicines Throw ResourceNotFound when patient not found")
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
        @DisplayName("Verify that getPatientMedicines Throw ResourceNotFoundException when medicines empty")
        void getPatientMedicines_throwResourceNotFoundMedicine() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            patient.setPatientMedicines(new ArrayList<>());
            //When
            assertThatThrownBy(() -> medicineTest.getPatientMedicines(patient.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Patient doesn't have medicines");
            //Then
            verify(medicineDao, never()).selectPatientMedicines(patient.getId());
            assertThat(patient.getPatientMedicines()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPatientMedicineById test units")
    class MedicineService_getPatientMedicineById {
        @Test
        @DisplayName("Verify that getPatientMedicineById can invoke selectPatientMedicineById")
        void getPatientMedicineById_returnMedicine() {
            // Given
            int patientId = 1;
            int medicineId = 1;
            when(medicineDao.selectPatientMedicineById(patientId, medicineId))
                    .thenReturn(Optional.of(medicine));
            //When
            Medicine actual = medicineTest.getPatientMedicineById(patientId, medicineId);
            //Then
            verify(medicineDao).selectPatientMedicineById(patientId, medicineId);
            assertThat(actual).isNotNull();
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
                    .hasMessage("Medicine wasn't found");
            //Then
           verify(medicineDao).selectPatientMedicineById(patientId, invalidMedicineId);
        }
    }

    @Nested
    @DisplayName("deletePatientMedicineById test units")
    class MedicineService_deletePatientMedicineById {
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
            doThrow(new ResourceNotFoundException("Couldn't find medicine"))
                    .when(medicineDao).deletePatientMedicineById(patientId, invalidMedicineId);
            //When
            assertThatThrownBy(() -> medicineTest.deletePatientMedicineById(patientId, invalidMedicineId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Couldn't find medicine");
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
    class MedicineService_savePatientMedicine {
        @Test
        @DisplayName("Verify that savePatientMedicine can invoke saveMedicine dao")
        void savePatientMedicine_Success() {
            //Given
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.getBrandName()))
                    .thenReturn(false);
            when(patientDao.doesPatientExists(patient.getEmail())).thenReturn(true);
            //When
            medicineTest.savePatientMedicine(medicineRegistrationTest, patient);
            //Then
            verify(medicineDao).saveMedicine(any(Medicine.class));
            verify(medicineDao).doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.getBrandName());
            verify(patientDao).doesPatientExists(patient.getEmail());
        }

        @Test
        @DisplayName("Verify that savePatientMedicine throw DuplicateResourceException")
        void savePatientMedicine_throwDuplicateResourceException() {
            //Given
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.getBrandName()))
                    .thenReturn(true);
            //When
            assertThatThrownBy(() -> medicineTest.savePatientMedicine(medicineRegistrationTest, patient))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Patient's medicine (%s) already Exists".formatted(medicineRegistrationTest.getBrandName()));
            //Then
            verify(medicineDao, never()).saveMedicine(any());
            verify(medicineDao).doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.getBrandName());
        }

        @Test
        @DisplayName("Verify that savePatientMedicine throw ResourceNotFoundException")
        void savePatientMedicine_throwResourceNotFoundException() {
            //Given
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), medicineRegistrationTest.getBrandName()))
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
    class MedicineService_editMedicineDetails {

        @Test
        @DisplayName("Verify that savePatientMedicine can invoke updateMedicine for valid validBrandName")
        void editMedicineDetails_validBrandNameUpdated() {
            // Given
            int patientId = 1;
            int medicineId = 1;
            when(patientDao.selectPatientById(patientId)).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patientId, medicineId))
                    .thenReturn(Optional.of(medicine));
            String newValidBrandName = "Nevelob";
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), newValidBrandName))
                    .thenReturn(false);
            //When
            medicineTest.editMedicineDetails(patientId, medicineId,
                    MedicineUpdateRequest.builder().brandName(newValidBrandName).build());

            //Then
            ArgumentCaptor<Medicine> medicineArgumentCaptor = ArgumentCaptor.forClass(Medicine.class);
            verify(medicineDao).updateMedicine(medicineArgumentCaptor.capture());
            Medicine capturedMedicine = medicineArgumentCaptor.getValue();
            assertThat(capturedMedicine.getBrandName()).isEqualTo(medicine.getBrandName());
        }


        @Test
        @DisplayName("Verify that editMedicineDetails Throw DuplicateResourceException")
        void editMedicineDetails_duplicateBrandName() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medicine.getId()))
                    .thenReturn(Optional.of(medicine));
            String duplicateBrandName = "duplicateBrand";
            // since you want an existing medicine vs another medicine with same brand name
            when(medicineDao.doesPatientMedicineExists(patient.getEmail(), duplicateBrandName)).thenReturn(true);
            //When
            assertThatThrownBy(() ->
                    medicineTest.editMedicineDetails(patient.getId(), medicine.getId(),
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
            when(medicineDao.selectPatientMedicineById(patient.getId(), medicine.getId()))
                    .thenReturn(Optional.of(medicine));
            String newActiveIngredient = "pseudophenrine";
            //When
            medicineTest.editMedicineDetails(patient.getId(), medicine.getId(),
                    MedicineUpdateRequest.builder().activeIngredient(newActiveIngredient).build());
            //Then
            ArgumentCaptor<Medicine> MedicineArgumentCaptor = ArgumentCaptor.forClass(Medicine.class);
            // ensure that medicineDao has been called for the updated(captured) Medicine object
            verify(medicineDao).updateMedicine(MedicineArgumentCaptor.capture());
            Medicine capturedMedicine = MedicineArgumentCaptor.getValue();
            // Ensure that the Medicine's Active Ingredient has been updated
            assertThat(capturedMedicine.getActiveIngredient()).isEqualTo(medicine.getActiveIngredient());
        }

        @Test
        @DisplayName("Verify that editMedicineDetails can invoke updateMedicine for validTimesDaily")
        void editMedicineDetails_validTimesDailyUpdated() {
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medicine.getId()))
                    .thenReturn(Optional.of(medicine));
            int newTimesDaily = -1;
            //When
            medicineTest.editMedicineDetails(patient.getId(), medicine.getId(),
                    MedicineUpdateRequest.builder().timesDaily(newTimesDaily).build());
            //Then
            ArgumentCaptor<Medicine> MedicineArgumentCaptor = ArgumentCaptor.forClass(Medicine.class);
            // ensure that medicineDao has been called for the updated(captured) Medicine object
            verify(medicineDao).updateMedicine(MedicineArgumentCaptor.capture());
            Medicine capturedMedicine = MedicineArgumentCaptor.getValue();
            // Ensure that the Medicine's Times Daily has been updated
            assertThat(capturedMedicine.getTimesDaily()).isEqualTo(medicine.getTimesDaily());
        }

        @Test
        @DisplayName("Verify that editMedicineDetails can invoke updateMedicine for valid Interactions")
        void editMedicineDetails_validInteractionUpdated() {
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medicine.getId()))
                    .thenReturn(Optional.of(medicine));
            List<String> validInteractions = Arrays.asList("Don't take before coding", "take before sleep");
            //When
            medicineTest.editMedicineDetails(patient.getId(), medicine.getId(),
                    MedicineUpdateRequest.builder().interactions(validInteractions).build());
            //Then
            ArgumentCaptor<Medicine> MedicineArgumentCaptor = ArgumentCaptor.forClass(Medicine.class);
            // ensure that patientDao has been called for the updated(captured) Patient object
            verify(medicineDao).updateMedicine(MedicineArgumentCaptor.capture());
            Medicine capturedMedicine = MedicineArgumentCaptor.getValue();
            // Ensure that the patient's email has been updated I.E captured and patient in db emails are the same
            assertThat(capturedMedicine.getInteractions()).isEqualTo(medicine.getInteractions());
        }

        @Test
        @DisplayName("Verify that editMedicineDetails can invoke updateMedicine for valid Instructions")
        void editMedicineDetails_validInstructionsUpdated() {
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medicine.getId()))
                    .thenReturn(Optional.of(medicine));
            String validInstructions = "interacts with cold medicines";
            //When
            medicineTest.editMedicineDetails(patient.getId(), medicine.getId(),
                    MedicineUpdateRequest.builder().instructions(validInstructions).build());
            //Then
            ArgumentCaptor<Medicine> MedicineArgumentCaptor = ArgumentCaptor.forClass(Medicine.class);
            // ensure that patientDao has been called for the updated(captured) Patient object
            verify(medicineDao).updateMedicine(MedicineArgumentCaptor.capture());
            Medicine capturedMedicine = MedicineArgumentCaptor.getValue();
            // Ensure that the patient's email has been updated I.E captured and patient in db emails are the same
            assertThat(capturedMedicine.getInstructions()).isEqualTo(medicine.getInstructions());
        }

        @Test
        @DisplayName("Verify that editMedicineDetails Throw UpdateException")
        void editMedicineDetails_noChangesBrandName() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            when(medicineDao.selectPatientMedicineById(patient.getId(), medicine.getId()))
                    .thenReturn(Optional.of(medicine));
            String sameBrandName = medicine.getBrandName();
            //When
            assertThatThrownBy(() -> medicineTest.editMedicineDetails(patient.getId(), medicine.getId(),
                    MedicineUpdateRequest.builder().brandName(sameBrandName).build()))
                    .isInstanceOf(UpdateException.class)
                    .hasMessage("no data changes found");
            //Then
            verify(medicineDao, never()).updateMedicine(any());
        }


    }
}