package com.simplesolutions.medicinesmanager.service.patient;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientDTOMapper;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientUpdateRequest;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("Tests for Patient service clas")
class PatientServiceTest {
    @Mock
    PatientDao patientDao;
    @Mock
    PasswordEncoder passwordEncoder;
    PatientService patientServiceTest;
    PatientDTOMapper patientDTOMapper;
    Faker faker;
    // patient for unit tests
    Patient patient;
    // expected patient dto
    PatientResponseDTO expected;
    // patient for constrained patient tests
    PatientRegistrationRequest patientRegistrationTest;
    // validator for testing validation
    LocalValidatorFactoryBean validatorFactory;

    @BeforeEach
    void setUp() {
        patientDTOMapper = new PatientDTOMapper();
        patientServiceTest = new PatientService(patientDao, patientDTOMapper, passwordEncoder);

        faker = new Faker();
        patient = Patient.builder()
                .id(1)
                .email(faker.internet().safeEmailAddress() + "-" + UUID.randomUUID())
                .password(faker.internet().password() + "U@")
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .age(faker.number().randomDigitNotZero())
                .build();
        expected = patientDTOMapper.apply(patient);
        patientRegistrationTest = createPatientRegistrationRequest(
                faker.internet().safeEmailAddress() + "-" + UUID.randomUUID());

        validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();
    }

    private PatientRegistrationRequest createPatientRegistrationRequest(String email) {
        return new PatientRegistrationRequest(
                email,
                faker.internet().password() + "U@",
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().randomDigitNotZero()
        );
    }

    @AfterEach
    void tearDown() {
        patientDao.deletePatientById(patient.getId());
    }

    @Nested
    @DisplayName("getAllPatients Unit test")
    class PatientService_getAllPatients {
        @Test
        @DisplayName("Verify that getAllPatients() can invoke selectAllPatients() dao")
        void getAllPatients_returnPatients() {
            //Given
            when(patientDao.selectAllPatients()).thenReturn(Collections.singletonList(patient));
            //When
            patientServiceTest.getAllPatients();
            //Then
            verify(patientDao).selectAllPatients();
            assertThat(patientServiceTest.getAllPatients()).isNotEmpty();
        }

        @Test
        @DisplayName("Verify that getAllPatients() return empty list")
        void getAllPatients_ThrowsResourceNotFound() {
            //Given
            when(patientDao.selectAllPatients()).thenReturn(Collections.emptyList());
            //When
            List<PatientResponseDTO> actual = patientServiceTest.getAllPatients();
            //Then
            verify(patientDao).selectAllPatients();
            assertThat(actual).isNotNull();
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPatientById unit test")
    class PatientService_getPatientById {
        @Test
        @DisplayName("Verify that getPatientById() can invoke selectPatientById() dao")
        void getPatientById_returnsPatient() {
            // Given
            int patientId = 1;
            when(patientDao.selectPatientById(patientId)).thenReturn(Optional.of(patient));
            //When
            PatientResponseDTO actual = patientServiceTest.getPatientById(patientId);
            //Then
            verify(patientDao).selectPatientById(patientId);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Verify that getPatientById() Throws ResourceNotFound when id is invalid")
        void getPatientById_ThrowsResourceNotFound() {
            // Given
            int invalidPatientId = -1;
            when(patientDao.selectPatientById(invalidPatientId))
                    .thenReturn(Optional.empty());
            //When
            assertThatThrownBy(() -> patientServiceTest.getPatientById(invalidPatientId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("patient with id %s not found".formatted(invalidPatientId));
            //Then
            verify(patientDao).selectPatientById(invalidPatientId);

        }
    }

    @Nested
    @DisplayName("getPatientEntityById unit test")
    class PatientService_getPatientEntityById {
        @Test
        @DisplayName("Verify that getPatientEntityById() can invoke selectPatientById() dao")
        void getPatientEntityById_returnsPatient() {
            // Given
            int patientId = 1;
            when(patientDao.selectPatientById(patientId)).thenReturn(Optional.of(patient));
            //When
            Patient actual = patientServiceTest.getPatientEntityById(patientId);
            //Then
            verify(patientDao).selectPatientById(patientId);
            assertThat(actual).isEqualTo(patient);
        }

        @Test
        @DisplayName("Verify that getPatientById() Throws ResourceNotFound when id is invalid")
        void getPatientEntityById_ThrowsResourceNotFound() {
            // Given
            int invalidPatientId = -1;
            when(patientDao.selectPatientById(invalidPatientId))
                    .thenReturn(Optional.empty());
            //When
            assertThatThrownBy(() -> patientServiceTest.getPatientEntityById(invalidPatientId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("patient with id %s not found".formatted(invalidPatientId));
            //Then
            verify(patientDao).selectPatientById(invalidPatientId);

        }
    }

    @Nested
    @DisplayName("getPatientByEmail unit test")
    class PatientService_getPatientByEmail {
        @Test
        @DisplayName("Verify that getPatientByEmail() can invoke selectPatientByEmail() dao")
        void getPatientByEmail_returnsPatient() {
            // Given
            when(patientDao.selectPatientByEmail(patient.getEmail()))
                    .thenReturn(Optional.of(patient));
            //When
            PatientResponseDTO actual = patientServiceTest.getPatientByEmail(patient.getEmail());
            //Then
            verify(patientDao).selectPatientByEmail(patient.getEmail());
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Verify that getPatientById() Throws ResourceNotFound when id is invalid")
        void getPatientById_ThrowsResourceNotFound() {
            // Given
            when(patientDao.selectPatientByEmail(patient.getEmail()))
                    .thenReturn(Optional.empty());
            //When
            assertThatThrownBy(() -> patientServiceTest.getPatientByEmail(patient.getEmail()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("patient with email %s was not found".formatted(patient.getEmail()));
            //Then
            verify(patientDao).selectPatientByEmail(patient.getEmail());

        }
    }

    @Nested
    @DisplayName("doesPatientExists unit test")
    class PatientService_doesPatientExists {

        @Test
        @DisplayName("Verify that doesPatientExists() can invoke doesPatientExists() in dao")
        void doesPatientExists_returnTrue() {
            // Given
            String email = "vaildEmail@example.com";
            when(patientDao.doesPatientExists(email)).thenReturn(true);
            //When
            boolean actual = patientServiceTest.doesPatientExists(email);
            //Then
            verify(patientDao).doesPatientExists(email);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Verify that doesPatientExists() throw ThrowResourceNotFound when email is invalid")
        void doesPatientExists_ThrowResourceNotFound() {
            // Given
            String invalidEmail = "invalidEmail@example.com";
            when(patientDao.doesPatientExists(invalidEmail)).thenThrow(ResourceNotFoundException.class);
            //When
            assertThatThrownBy(() -> patientServiceTest.doesPatientExists(invalidEmail))
                    .isInstanceOf(ResourceNotFoundException.class);
            //Then
            verify(patientDao).doesPatientExists(invalidEmail);
        }
    }

    @Nested
    @DisplayName("savePatient unit tests")
    class PatientService_savePatient {

        @Test
        @DisplayName("Verify that savePatient() can invoke savePatient() with bcrypt password in dao")
        void savePatient_Success() {
            // Given
            when(patientDao.doesPatientExists(patientRegistrationTest.getEmail())).thenReturn(false);
            String passwordHash = "$@#$Hashed@$!PasswordGF#!";
            when(passwordEncoder.encode(patientRegistrationTest.getPassword()))
                    .thenReturn(passwordHash);
            //When
            patientServiceTest.savePatient(patientRegistrationTest);
            ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
            //Then
            verify(patientDao).savePatient(patientArgumentCaptor.capture());
            verify(patientDao).doesPatientExists(patientRegistrationTest.getEmail());
            Patient capturedPatient = patientArgumentCaptor.getValue();
            assertThat(capturedPatient.getPassword()).isEqualTo(passwordHash);
        }

        @Test
        @DisplayName("Verify that savePatient() Throws PatientRegistrationConstraints_EmptyEmail ")
        void savePatient_ConstraintsException_emptyEmail() {
            //Given
            PatientRegistrationRequest emptyEmailPatient = createPatientRegistrationRequest("");
            //When
            Set<ConstraintViolation<PatientRegistrationRequest>> violations =
                    validatorFactory.validate(emptyEmailPatient);
            //Then
            verify(patientDao, never()).savePatient(any());
            assertThat(violations).hasSizeBetween(1, 5);
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains("Field is Required", "Must be a valid email address");

        }

        @Test
        @DisplayName("Verify that savePatient() Throws PatientRegistrationConstraints_invalidEmail ")
        void savePatient_ConstraintsException_invalidEmail() {
            //Given
            PatientRegistrationRequest invalidEmailPatient = createPatientRegistrationRequest("dsadasd");
            //When
            Set<ConstraintViolation<PatientRegistrationRequest>> violations =
                    validatorFactory.validate(invalidEmailPatient);
            //Then
            verify(patientDao, never()).savePatient(any());
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("Must be a valid email address");
        }

        @Test
        @DisplayName("Verify that savePatient() Throws DuplicateResource")
        void savePatient_DuplicateResourceException() {
            // Given
            when(patientDao.doesPatientExists(patientRegistrationTest.getEmail())).thenReturn(true);
            //When
            assertThatThrownBy(() -> patientServiceTest.savePatient(patientRegistrationTest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Patient with email %s already exists".formatted(patientRegistrationTest.getEmail()));
            //Then
            verify(patientDao).doesPatientExists(patientRegistrationTest.getEmail());
            verify(patientDao, never()).savePatient(any());
        }
    }

    @Nested
    @DisplayName("deletePatient  unit test")
    class PatientService_deletePatient {

        @Test
        @DisplayName("Verify that deletePatient() can invoke deletePatientById() in dao")
        void deletePatient_Success() {
            //Given
            int validId = 1;
            when(patientDao.selectPatientById(validId)).thenReturn(Optional.of(patient));
            //When
            patientServiceTest.deletePatient(validId);
            //Then
            verify(patientDao).deletePatientById(patient.getId());

        }

        @Test
        @DisplayName("Verify that deletePatient() Throws ResourceNotFound When invalidId ")
        void deletePatient_ThrowsResourceNotFound() {
            //Given
            int invalidId = -1;
            when(patientDao.selectPatientById(invalidId)).thenReturn(Optional.empty());
            //When
            assertThatThrownBy(() -> patientServiceTest.deletePatient(invalidId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("patient with id %s not found".formatted(invalidId));
            //Then
            verify(patientDao, never()).deletePatientById(invalidId);

        }
    }

    @Nested
    @DisplayName("Edit Patient password unit tests")
    class PatientService_editPatientPassword {
        @Test
        @DisplayName("Verify that editPatientDetails can update the password")
        void editPatientDetails_Success() {
            // Given
            String currentPassword = "CurrentP@ssword123";
            String newPassword = "NewP@ssword123";

            PatientUpdateRequest request = PatientUpdateRequest.builder()
                    .currentPassword(currentPassword)
                    .password(newPassword)
                    .build();
            when(patientDao.selectPatientById(patient.getId()))
                    .thenReturn(Optional.of(patient));
            when(patientServiceTest.isOldPasswordValid(currentPassword, patient))
                    .thenReturn(true);
            // When
            patientServiceTest.editPatientPassword(patient.getId(), request);

            // Then
            verify(patientDao).updatePatient(patient);
        }

        @Test
        @DisplayName("Verify that editPatientDetails throw updateException with invalid new password")
        void editPatientDetails_throwUpdateException_invalidNewPassword() {
            // Given
            String currentPassword = "CurrentP@ssword123";
            String newPassword = "";

            PatientUpdateRequest request = PatientUpdateRequest.builder()
                    .currentPassword(currentPassword)
                    .password(newPassword)
                    .build();
            when(patientDao.selectPatientById(patient.getId()))
                    .thenReturn(Optional.of(patient));
            when(patientServiceTest.isOldPasswordValid(currentPassword, patient))
                    .thenReturn(true);

            // When
            assertThatThrownBy(() -> patientServiceTest.editPatientPassword(patient.getId(), request))
                    .isInstanceOf(UpdateException.class)
                    .hasMessage("New password Can't be empty");

            // Then
            verify(patientDao, never()).updatePatient(any());
        }

        @Test
        @DisplayName("Verify that editPatientDetails throw updateException with invalid current password")
        void editPatientDetails_throwUpdateException() {
            // Given
            String currentPassword = "invalidPass";
            String newPassword = "NewP@ssword123";

            PatientUpdateRequest request = PatientUpdateRequest.builder()
                    .currentPassword(currentPassword)
                    .password(newPassword)
                    .build();
            when(patientDao.selectPatientById(patient.getId()))
                    .thenReturn(Optional.of(patient));
            when(patientServiceTest.isOldPasswordValid(currentPassword, patient))
                    .thenReturn(false);
            // When
            assertThatThrownBy(() -> patientServiceTest.editPatientPassword(patient.getId(), request))
                    .isInstanceOf(UpdateException.class)
                    .hasMessage("Current password is incorrect");

            // Then
            verify(patientDao, never()).updatePatient(any());
        }

        @Test
        @DisplayName("Verify that editPatientDetails throw updateException with null or empty current password")
        void editPatientDetails_throwUpdateException_emptyCurrentPassword() {
            // Given
            String currentPassword = "";
            String newPassword = "NewP@ssword123";

            PatientUpdateRequest request = PatientUpdateRequest.builder()
                    .currentPassword(currentPassword)
                    .password(newPassword)
                    .build();
            when(patientDao.selectPatientById(patient.getId()))
                    .thenReturn(Optional.of(patient));
            // When
            assertThatThrownBy(() -> patientServiceTest.editPatientPassword(patient.getId(), request))
                    .isInstanceOf(UpdateException.class)
                    .hasMessage("Current password can't be null or empty");

            // Then
            verify(patientDao, never()).updatePatient(any());
        }
    }

    @Nested
    @DisplayName("editPatientDetails unit tests")
    class PatientService_editPatientDetails {
        @Test
        @DisplayName("Verify that editPatientDetails can invoke updatePatient for validEmail")
        void editPatientDetails_validEmailUpdated() {
            // Given
            // valid email means that it's not null, nor it's the same email, nor it's already taken by other users
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            String newValidEmail = "Ahmed@example.com";
            when(patientDao.doesPatientExists(newValidEmail)).thenReturn(false);
            //When
            patientServiceTest.editPatientDetails(patient.getId(),
                    PatientUpdateRequest.builder().email(newValidEmail).build());
            //Then
            ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
            // ensure that patientDao has been called for the updated(captured) Patient object
            verify(patientDao).updatePatient(patientArgumentCaptor.capture());
            Patient capturedPatient = patientArgumentCaptor.getValue();
            // Ensure that the patient's email has been updated I.E captured and patient in db emails are the same
            assertThat(capturedPatient.getEmail()).isEqualTo(patient.getEmail());
        }

        @Test
        @DisplayName("Verify that editPatientDetails throw ResourceNotFoundException")
        void editPatientDetails_throwResourceNotFoundException() {
            // given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.empty());
            // when
            assertThatThrownBy(() ->
                    patientServiceTest.editPatientDetails(patient.getId(),
                            PatientUpdateRequest.builder().email("nonExistentPatientEmail").build()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("patient with id %s not found".formatted(patient.getId()));
            // then
            verify(patientDao).selectPatientById(patient.getId());

        }

        @Test
        @DisplayName("Verify that editPatientDetails Throw DuplicateResourceException")
        void editPatientDetails_duplicateEmail() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            String duplicateEmail = "duplicate@example.com";
            // since you want an existing patient vs another patient with the same email
            when(patientDao.doesPatientExists(duplicateEmail)).thenReturn(true);
            //When
            assertThatThrownBy(() ->
                    patientServiceTest.editPatientDetails(patient.getId(),
                            PatientUpdateRequest.builder().email(duplicateEmail).build()))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("email already registered");
            //Then
            verify(patientDao, never()).updatePatient(any());
        }

        @Test
        @DisplayName("Verify that editPatientDetails can invoke updatePatient for validFirstname")
        void editPatientDetails_validFirstnameUpdated() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            String newFirstname = "Ahmed";
            //When
            patientServiceTest.editPatientDetails(patient.getId(),
                    PatientUpdateRequest.builder().firstname(newFirstname).build());
            //Then
            ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
            // ensure that patientDao has been called for the updated(captured) Patient object
            verify(patientDao).updatePatient(patientArgumentCaptor.capture());
            Patient capturedPatient = patientArgumentCaptor.getValue();
            // Ensure that the patient's email has been updated I.E captured and patient in db emails are the same
            assertThat(capturedPatient.getFirstname()).isEqualTo(patient.getFirstname());
        }

        @Test
        @DisplayName("Verify that editPatientDetails can invoke updatePatient for validLastname")
        void editPatientDetails_validLastnameUpdated() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            String newLastname = "Ibrahim";
            //When
            patientServiceTest.editPatientDetails(patient.getId(),
                    PatientUpdateRequest.builder().lastname(newLastname).build());
            //Then
            ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
            // ensure that patientDao has been called for the updated(captured) Patient object
            verify(patientDao).updatePatient(patientArgumentCaptor.capture());
            Patient capturedPatient = patientArgumentCaptor.getValue();
            // Ensure that the patient's email has been updated I.E captured and patient in db emails are the same
            assertThat(capturedPatient.getLastname()).isEqualTo(patient.getLastname());
        }

        @Test
        @DisplayName("Verify that editPatientDetails can invoke updatePatient for valid age")
        void editPatientDetails_validAgeUpdated() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            Integer age = 29;
            //When
            patientServiceTest.editPatientDetails(patient.getId(),
                    PatientUpdateRequest.builder().age(age).build());
            //Then
            ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
            // ensure that patientDao has been called for the updated(captured) Patient object
            verify(patientDao).updatePatient(patientArgumentCaptor.capture());
            Patient capturedPatient = patientArgumentCaptor.getValue();
            // Ensure that the patient's email has been updated I.E captured and patient in db emails are the same
            assertThat(capturedPatient.getAge()).isEqualTo(patient.getAge());
        }


        @Test
        @DisplayName("Verify that editPatientDetails Throw UpdateException")
        void editPatientDetails_noChangesEmail() {
            // Given
            when(patientDao.selectPatientById(patient.getId())).thenReturn(Optional.of(patient));
            String sameEmail = patient.getEmail();
            //When
            assertThatThrownBy(() -> patientServiceTest.editPatientDetails(patient.getId(),
                    PatientUpdateRequest.builder().email(sameEmail).build()))
                    .isInstanceOf(UpdateException.class)
                    .hasMessage("no data changes found");
            //Then
            verify(patientDao, never()).updatePatient(any());
        }
    }
}