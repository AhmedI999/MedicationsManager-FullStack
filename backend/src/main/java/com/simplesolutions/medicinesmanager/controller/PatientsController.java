package com.simplesolutions.medicinesmanager.controller;

import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.dto.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.PatientResponse;
import com.simplesolutions.medicinesmanager.dto.PatientUpdateRequest;
import com.simplesolutions.medicinesmanager.service.patient.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class PatientsController {
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<Patient> patientList = patientService.getAllPatients();
        List<PatientResponse> responseList = patientList.stream()
                .map(PatientResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }
    // todo patient medicines no longer retrieved with the patient in below endpoint. may create separate one if needed
    @GetMapping("{patientId}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable("patientId") Integer id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(PatientResponse.builder()
                .email(patient.getEmail())
                .firstname(patient.getFirstname())
                .lastname(patient.getLastname())
                .age(patient.getAge())
                .build());
    }

    @PostMapping
    private ResponseEntity<String> savePatient(@RequestBody @Valid PatientRegistrationRequest request){
        patientService.savePatient(request);
        // for now, we will return success string
        return ResponseEntity.ok("Patient saved successfully!");
    }

    @DeleteMapping("{patientId}")
    public ResponseEntity<String> deletePatient(@PathVariable("patientId") Integer id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok("Patient deleted successfully");
    }

    @PutMapping("{patientId}")
    public ResponseEntity<Patient> editPatientDetails(@PathVariable("patientId") Integer patientId,
                                                              @RequestBody @Valid PatientUpdateRequest request){
        patientService.editPatientDetails(patientId, request);
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

}

