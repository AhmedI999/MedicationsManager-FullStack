package com.simplesolutions.medicinesmanager.controller;

import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientUpdateRequest;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.RegistrationConstraintsException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.security.jwt.JWTUtil;
import com.simplesolutions.medicinesmanager.service.patient.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "User (Patients) controller")
public class PatientsController {
    private final PatientService patientService;
    private final JWTUtil jwtUtil;

    // MESSAGES
    @Value("#{'${patient.save-endpoint.success.message}'}")
    private String PATIENT_SAVED_MESSAGE;
    @Value("#{'${patient.delete-endpoint.success.message}'}")
    private String PATIENT_DELETE_MESSAGE;
    @Value("#{'${patient.password-endpoint.success.message}'}")
    private String PATIENT_CHANGE_PASSWORD_MESSAGE;


    @Operation(
            description = "Get endpoint for patients",
            summary = "This endpoint retrieve all patients, mapped by PatientResponseDto",
            responses = {
                    @ApiResponse(
                        description = "Success",
                        responseCode = "200",
                        useReturnTypeSchema = true,
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PatientResponseDTO.class)),
                        headers = {
                            @Header(
                                    name = "Authorization",
                                    description = "bearer token",
                                    required = true
                            )
                        }
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InsufficientAuthenticationException.class),
                                    examples = @ExampleObject(
                                            name = "Unauthorized Exception returned Json",
                                            value = """
                                                    {
                                                        "path": "/error",
                                                        "message": "Full authentication is required to access this resource",
                                                        "statusCode": 403,
                                                        "localDateTime": "Current Time"
                                                    }"""
                                    )
                            )
                    ),
            }

    )
    @GetMapping
    public List<PatientResponseDTO> getAllPatients() {
        return patientService.getAllPatients();
    }
    @Operation(
            description = "Get endpoint for a patient with id",
            summary = "This endpoint retrieve a patient using the patient's id",
            parameters = {
                    @Parameter(
                            name = "patientId",
                            description = "Id of the required patient",
                            in = ParameterIn.PATH,
                            example = "1",
                            required = true)
            },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            useReturnTypeSchema = true,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PatientResponseDTO.class))


                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InsufficientAuthenticationException.class),
                                    examples = @ExampleObject(
                                            name = "Unauthorized Exception returned Json",
                                            value = """
                                                    {
                                                        "path": "/error",
                                                        "message": "Full authentication is required to access this resource",
                                                        "statusCode": 403,
                                                        "localDateTime": "Current Time"
                                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            description = "Not Found, Message: patient with id 1 not found",
                            responseCode = "404",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResourceNotFoundException.class),
                                    examples = @ExampleObject(
                                            name = "Patient Not Found",
                                            value = """
                                                    {
                                                        "path": "/api/v1/patients/id/1",
                                                        "message": "patient with id 1 not found",
                                                        "statusCode": 404,
                                                        "localDateTime": "Current time"
                                                    }"""
                                    )
                            )
                    )
            }

    )
    @GetMapping("id/{patientId}")
    public PatientResponseDTO getPatient(@PathVariable("patientId") Integer id) {
        return patientService.getPatientById(id);
    }
    @Operation(
            description = "Get endpoint for retrieving a patient with Email",
            summary = "This endpoint retrieve a patient using the patient's Email",
            parameters = {
                    @Parameter(
                            name = "email",
                            description = "Email of the required patient.\n" +
                                    "email decoded with UTF_8 to ensure no complications with email syntax ",
                            in = ParameterIn.PATH,
                            example = "John@Example.com",
                            required = true)
            },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            useReturnTypeSchema = true,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PatientResponseDTO.class))


                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InsufficientAuthenticationException.class),
                                    examples = @ExampleObject(
                                            name = "Unauthorized Exception returned Json",
                                            value = """
                                                    {
                                                        "path": "/error",
                                                        "message": "Full authentication is required to access this resource",
                                                        "statusCode": 403,
                                                        "localDateTime": "Current time"
                                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            description = "Not Found, Message: patient with email John@Example.com not found",
                            responseCode = "404",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResourceNotFoundException.class),
                                    examples = @ExampleObject(
                                            name = "Patient Not Found",
                                            value = """
                                                    {
                                                        "path": "/api/v1/patients/John@Example.com",
                                                        "message": "patient with email John@Example.com was not found",
                                                        "statusCode": 404,
                                                        "localDateTime": "Current time"
                                                    }"""
                                    )
                            )
                    )
            }

    )
    @GetMapping("{email}")
    public PatientResponseDTO getPatientByEmail(@PathVariable("email") String encodedEmail) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        return patientService.getPatientByEmail(email);
    }
    @Operation(
            description = "POST endpoint for saving a new patient",
            summary = "This endpoint saves a patient and require a validated Json body",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Endpoint require Json body of validated PatientRegistrationRequest object",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PatientRegistrationRequest.class),
                        examples = @ExampleObject(name = "Patient Registration Request",
                               value = "{\"email\":\"JohnDoe@Gmail.com\",\"password\":\"P@ssword123\",\"firstname\":\"John\",\"lastname\":\"Doe\",\"age\":30}"))
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(name = "Response for 200 requests",
                                            value = "Patient with email JohnDoe@Gmail.com saved successfully"))

                    ),
                    @ApiResponse(
                            description = "Bad Request, message depends on the field that failed validation",
                            responseCode = "400",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RegistrationConstraintsException.class),
                                    examples = @ExampleObject(name = "exception in the case of invalid firstname",
                                            value = """
                                                    {\s
                                                     "path": "/api/v1/patients",
                                                     "timestamp": "2024-01-17T12:18:26.560+00:00",
                                                     "status": 400,
                                                     "invalidFields": [
                                                     {
                                                     "field": "firstname",
                                                     "error": "Only alphabetic characters are allowed"
                                                            }
                                                     ]
                                                     }""")
                            )
                    ),
            }

    )
    @PostMapping
    private ResponseEntity<?> savePatient(@RequestBody @Valid PatientRegistrationRequest request){
        patientService.savePatient(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .body(PATIENT_SAVED_MESSAGE.formatted(request.email()));
    }
    @Operation(
            description = "DELETE endpoint for deleting a patient using patient's Id",
            summary = "This endpoint delete a patient using the patient's Id",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "id of the required patient.",
                            in = ParameterIn.PATH,
                            example = "1",
                            required = true)
            },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            useReturnTypeSchema = true,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(name = "response in the case of successful deletion",
                                    value = "Patient deleted successfully"))


                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InsufficientAuthenticationException.class),
                                    examples = @ExampleObject(
                                            name = "Unauthorized Exception returned Json",
                                            value = """
                                                    {
                                                        "path": "/error",
                                                        "message": "Full authentication is required to access this resource",
                                                        "statusCode": 403,
                                                        "localDateTime": "Current time"
                                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            description = "Not Found, Message: patient with id 1 not found",
                            responseCode = "404",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResourceNotFoundException.class),
                                    examples = @ExampleObject(
                                            name = "Patient Not Found",
                                            value = """
                                                    {
                                                        "path": "/api/v1/patients/John@Example.com",
                                                        "message": "patient with id 1 not found",
                                                        "statusCode": 404,
                                                        "localDateTime": "Current time"
                                                    }"""
                                    )
                            )
                    )
            }

    )
    @DeleteMapping("{patientId}")
    public ResponseEntity<String> deletePatient(@PathVariable("patientId") Integer id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(PATIENT_DELETE_MESSAGE);
    }

    @Operation(
            description = "PUT endpoint for editing patient details",
            summary = "This endpoint updates a patient and require the fields to be changed",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "id of the patient.",
                            in = ParameterIn.PATH,
                            example = "1",
                            required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Endpoint require Json body of validated fields from PatientUpdateRequest object",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PatientUpdateRequest.class),
                            examples = @ExampleObject(name = "Patient Update Request",
                                    value = """
                                            {
                                                "age": 31
                                            }"""))
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PatientResponseDTO.class),
                                    examples = @ExampleObject(name = "Response for 200 requests",
                                            value = """
                                            {
                                                "id": 1,
                                                "email": "JohnDoe@Gmail.com",
                                                "firstname": "John",
                                                "age": 31,
                                                "roles": [
                                                    "ROLE_USER"
                                                ]
                                            }"""))

                    ),
                    @ApiResponse(
                            description = "Bad Request, message depends on the field that failed validation",
                            responseCode = "400",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RegistrationConstraintsException.class),
                                    examples = @ExampleObject(name = "exception in the case of invalid firstname",
                                            value = """
                                                    {\s
                                                     "path": "/api/v1/patients",
                                                     "timestamp": "current time",
                                                     "status": 400,
                                                     "invalidFields": [
                                                     {
                                                     "field": "firstname",
                                                     "error": "Only alphabetic characters are allowed"
                                                            }
                                                     ]
                                                     }""")
                            )
                    ),
                    @ApiResponse(
                            description = "Bad Request, No data changes found",
                            responseCode = "400 NoUpdate",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateException.class),
                                    examples = @ExampleObject(name = "No data changes found",
                                            summary = "This exception occurs when the user enter fields and it has the same value as before",
                                            value = """
                                                    {\s
                                                     "path": "/api/v1/patients/1",
                                                     "message": "no data changes found",
                                                     "statusCode": 400,
                                                     "localDateTime": "current time"
                                                     }""")
                            )
                    ),

            }

    )
    @PutMapping("{patientId}")
    public ResponseEntity<PatientResponseDTO> editPatientDetails(@PathVariable("patientId") Integer patientId,
                                                              @RequestBody @Valid PatientUpdateRequest request){
        patientService.editPatientDetails(patientId, request);
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }
    @Operation(
            description = "PUT endpoint for editing patient password",
            summary = "This endpoint updates the patient password and require validated current password and the new password",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "id of the patient.",
                            in = ParameterIn.PATH,
                            example = "1",
                            required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Endpoint require Json body of validated currentPassword and password(new password)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PatientUpdateRequest.class),
                            examples = @ExampleObject(name = "Required Patient password Update Request",
                                    value = """
                                            {
                                                "currentPassword": "CurrentPassword",
                                                "password":"NewPassword"
                                            }"""))
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(name = "Response for 200 requests",
                                            value = "Password Changed Successfully"))

                    ),
                    @ApiResponse(
                            description = "Bad Request, if new password failed validation",
                            responseCode = "400",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RegistrationConstraintsException.class),
                                    examples = @ExampleObject(name = "exception in the case of invalid new password",
                                            value = """
                                                    {\s
                                                        "path": "/api/v1/patients/9/change-password",
                                                        "timestamp": "2024-01-17T15:06:15.909+00:00",
                                                        "status": 400,
                                                        "invalidFields": [
                                                            {
                                                                "field": "password",
                                                                "error": "Password should contain at least 1 uppercase and 1 special Character"
                                                            }
                                                        ]
                                                    }""")
                            )
                    ),
                    @ApiResponse(
                            description = "Bad Request, old password is incorrect",
                            responseCode = "400 incorrectPass",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateException.class),
                                    examples = @ExampleObject(name = "Current password is incorrect",
                                            summary = "This exception occurs when the user enter incorrect current password",
                                            value = """
                                                    {\s
                                                     "path": "/api/v1/patients/9/change-password",
                                                    "message": "Current password is incorrect",
                                                    "statusCode": 400,
                                                    "localDateTime": "current time"
                                                     }""")
                            )
                    ),
                    @ApiResponse(
                            description = "Bad Request, old and new password are the same",
                            responseCode = "409",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "bearer token",
                                            required = true
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DuplicateResourceException.class),
                                    examples = @ExampleObject(name = "Current password and new passwords are the same",
                                            summary = "This exception occurs when the user enter the same current password",
                                            value = """
                                                    {\s
                                                        "path": "/api/v1/patients/9/change-password",
                                                        "message": "Passwords are identical",
                                                        "statusCode": 409,
                                                        "localDateTime": "2024-01-17T17:57:38.619406347"
                                                    }""")
                            )
                    )
            }

    )
    @PutMapping("/{patientId}/change-password")
    public ResponseEntity<String> changePatientPassword (@PathVariable("patientId") Integer patientId,
                                         @RequestBody @Valid PatientUpdateRequest request) {
        patientService.editPatientPassword(patientId, request);
        return ResponseEntity.ok(PATIENT_CHANGE_PASSWORD_MESSAGE);
    }

}

