package com.simplesolutions.medicinesmanager.controller;

import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.paylod.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.paylod.MedicineResponse;
import com.simplesolutions.medicinesmanager.paylod.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.service.medicine.MedicineService;
import com.simplesolutions.medicinesmanager.service.patient.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class PatientMedicinesController {
    private final PatientService patientService;
    private final MedicineService medicineService;

    @GetMapping("/{patientId}/medicines/{medicineId}")
    public ResponseEntity<MedicineResponse> getMedicine(@PathVariable("patientId") Integer patientId,
                                                        @PathVariable("medicineId") Integer medicineId){
        Medicine medicine = medicineService.getPatientMedicineById(patientId, medicineId);
        return ResponseEntity.ok(new MedicineResponse(medicine.getBrandName(), medicine.getActiveIngredient(),
                medicine.getTimesDaily(), medicine.getInstructions(), medicine.getInteractions()));
    }
    @GetMapping("{patientId}/medicines")
    public List<Medicine> getAllPatientMedicines(@PathVariable("patientId") Integer id){
        return medicineService.getPatientMedicines(id);
    }
    @PostMapping("/{patientId}/medicines")
    public ResponseEntity<String> savePatientMedicine(@PathVariable("patientId") Integer patientId,
                                                      @RequestBody @Valid MedicineRegistrationRequest request){
        Patient patient = patientService.getPatientById(patientId);
        medicineService.savePatientMedicine(request, patient);
        return ResponseEntity.ok("Patient medicine saved successfully");
    }
    @PutMapping("{patientId}/medicines/{medicineId}")
    public ResponseEntity<Medicine> editMedicineDetails(@PathVariable("patientId") Integer patientId,
                                                        @PathVariable("medicineId") Integer medicineId,
                                                        @RequestBody @Valid MedicineUpdateRequest request){
        medicineService.editMedicineDetails(patientId, medicineId, request);
        return ResponseEntity.ok(medicineService.getPatientMedicineById(patientId, medicineId));
    }
    @DeleteMapping ("/{patientId}/medicines/{medicineId}")
    public ResponseEntity<String> deleteMedicine(@PathVariable("patientId") Integer patientId,
                                                 @PathVariable("medicineId") Integer medicineId){
        medicineService.deletePatientMedicineById(patientId, medicineId);
        return ResponseEntity.ok("Medicine deleted Successfully");
    }
}
