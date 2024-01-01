package com.simplesolutions.medicinesmanager.controller;

import com.simplesolutions.medicinesmanager.dto.patientdto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientResponseDTO;
import com.simplesolutions.medicinesmanager.dto.patientdto.PatientUpdateRequest;
import com.simplesolutions.medicinesmanager.security.jwt.JWTUtil;
import com.simplesolutions.medicinesmanager.service.patient.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class PatientsController {
    private final PatientService patientService;
    private final JWTUtil jwtUtil;

    @GetMapping
    public List<PatientResponseDTO> getAllPatients() {
        return patientService.getAllPatients();
    }
    @GetMapping("{patientId}")
    public PatientResponseDTO getPatient(@PathVariable("patientId") Integer id) {
        return patientService.getPatientById(id);
    }

    @PostMapping
    private ResponseEntity<?> savePatient(@RequestBody @Valid PatientRegistrationRequest request){
        patientService.savePatient(request);
        String jwtToken = jwtUtil.issueToken(request.getEmail(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .body("Patient with email:%s. is saved successfully".formatted(request.getEmail()));
    }

    @DeleteMapping("{patientId}")
    public ResponseEntity<String> deletePatient(@PathVariable("patientId") Integer id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok("Patient deleted successfully");
    }

    @PutMapping("{patientId}")
    public ResponseEntity<PatientResponseDTO> editPatientDetails(@PathVariable("patientId") Integer patientId,
                                                              @RequestBody @Valid PatientUpdateRequest request){
        patientService.editPatientDetails(patientId, request);
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

}

