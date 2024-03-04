package com.simplesolutions.medicinesmanager.security.auth;

import com.simplesolutions.medicinesmanager.dto.email.EmailVerificationRequest;
import com.simplesolutions.medicinesmanager.service.patient.PatientService;
import com.simplesolutions.medicinesmanager.service.patient.email.EmailConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final PatientService patientService;

    // MESSAGES
    @Value("#{'${user.login-endpoint.success.message}'}")
    private String USER_LOGGED_MESSAGE;
    @Value("#{'${user.confirm-endpoint.success.message}'}")
    private String USER_EMAIL_VERIFIED_MESSAGE;
    @Value("#{'${user.sendVerification-endpoint.success.message}'}")
    private String USER_EMAIL_VERIFICATION_REQUEST_MESSAGE;

    @PostMapping("login")
    public ResponseEntity<?> login (@RequestBody AuthenticationRequest request) {
        String jwtToken = authenticationService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .body(USER_LOGGED_MESSAGE);
    }
    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token){
        patientService.confirmEmail(token);
        return ResponseEntity.ok(USER_EMAIL_VERIFIED_MESSAGE);
    }
    @PostMapping("send-verification")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody EmailVerificationRequest request){
        patientService.createAndSendEmailVerification(request.email());
        return ResponseEntity.ok(USER_EMAIL_VERIFICATION_REQUEST_MESSAGE);
    }
}
