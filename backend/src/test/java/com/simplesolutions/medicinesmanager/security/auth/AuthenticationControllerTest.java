package com.simplesolutions.medicinesmanager.security.auth;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.security.jwt.JWTUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Authentication controller Integration Test")
class AuthenticationControllerTest {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    JWTUtil jwtUtil;
    static final String AUTHENTICATION_PATH = "/api/v1/auth";
    static final String PATIENT_PATH = "/api/v1/patients";
    Faker faker;
    PatientRegistrationRequest patientRequest;
    AuthenticationRequest request;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        patientRequest = new PatientRegistrationRequest(
                faker.internet().safeEmailAddress(),
                faker.internet().password() + "P@",
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().randomDigitNotZero()
        );
        request = new AuthenticationRequest(
                patientRequest.email(),
                patientRequest.password()
        );

    }

    @Test
    @DisplayName("Verify that an Unauthorized user can't login and throw BadCredentialsException")
    void login_throwBadCredentialsException() {
        // Logging in directly without saving patient
        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AuthenticationRequest.class)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Exception.class)
                .consumeWith(response -> {
                    Exception responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assertThat(responseBody.getMessage()).isEqualTo("Bad credentials");
                });
    }
    @Test
    void login() {
        // Saving the patient and check if the status is 200
        webTestClient.post()
                .uri(PATIENT_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(patientRequest), PatientRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();
        // Logging in with patient credentials
        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AuthenticationRequest.class)
                .exchange()
                .expectStatus().isOk()
                // checking that the token in AUTHORIZATION header is valid
                .expectHeader().value(HttpHeaders.AUTHORIZATION,
                        jwtToken -> assertThat(jwtUtil.isTokenValid(jwtToken, patientRequest.email())).isTrue())
                // checking the body of the response
                .expectBody(String.class)
                .consumeWith(response -> assertThat(response.getResponseBody())
                        .isEqualTo("You have logged in successfully"));

    }
}