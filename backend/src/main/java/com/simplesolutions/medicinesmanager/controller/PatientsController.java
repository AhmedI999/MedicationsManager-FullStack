package com.simplesolutions.medicinesmanager.controller;

import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.paylod.PatientRegistrationRequest;
import com.simplesolutions.medicinesmanager.paylod.PatientResponse;
import com.simplesolutions.medicinesmanager.paylod.PatientUpdateRequest;
import com.simplesolutions.medicinesmanager.service.patient.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class PatientsController {
    private final PatientService patientService;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("{patientId}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable("patientId") Integer id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok( new PatientResponse(patient.getEmail(), patient.getFirstname()
                , patient.getLastname(),patient.getAge(), patient.getPatientMedicines()));
    }

    @PostMapping
    private ResponseEntity<String> savePatient(@RequestBody @Valid PatientRegistrationRequest request){
        patientService.savePatient(request);
        // for now, we will return success string
        return ResponseEntity.ok("Patient saved successfully!");
    }

    @DeleteMapping("/{patientId}")
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

