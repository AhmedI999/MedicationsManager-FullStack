package com.simplesolutions.medicinesmanager.controller;

import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicationResponseDTO;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.service.medicine.MedicineService;
import com.simplesolutions.medicinesmanager.service.patient.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class PatientMedicationsController {
    private final PatientService patientService;
    private final MedicineService medicineService;

    // MESSAGES
    @Value("#{'${medications.save-endpoint.success.message}'}")
    private String MEDICATION_SAVED_MESSAGE;
    @Value("#{'${medications.delete-endpoint.success.message}'}")
    private String MEDICATION_DELETED_MESSAGE;

    @GetMapping("{patientId}/medicines/id/{medicineId}")
    public MedicationResponseDTO getMedicine(@PathVariable("patientId") Integer patientId,
                                                             @PathVariable("medicineId") Integer medicineId){
        return medicineService.getPatientMedicineById(patientId, medicineId);
    }
    @GetMapping("{patientId}/medicines/{brandName}")
    public MedicationResponseDTO getMedicineByBrandName(@PathVariable("patientId") Integer patientId,
                                                                        @PathVariable("brandName") String brandName){
        return medicineService.getPatientMedicineByBrandName(patientId, brandName);
    }
    @GetMapping("{patientId}/medicines")
    public List<MedicationResponseDTO> getAllPatientMedicines(@PathVariable("patientId") Integer id){
        return medicineService.getPatientMedicines(id);
    }
    @PostMapping("{patientId}/medicines")
    public ResponseEntity<String> savePatientMedicine(@PathVariable("patientId") Integer patientId,
                                                      @RequestBody @Valid MedicineRegistrationRequest request){
        Patient patient = patientService.getPatientEntityById(patientId);
        medicineService.savePatientMedicine(request, patient);
        return ResponseEntity.ok(MEDICATION_SAVED_MESSAGE);
    }
    @PutMapping("{patientId}/medicines/{medicineId}")
    public ResponseEntity<MedicationResponseDTO> editMedicineDetails(@PathVariable("patientId") Integer patientId,
                                                          @PathVariable("medicineId") Integer medicineId,
                                                          @RequestBody @Valid MedicineUpdateRequest request){
        medicineService.editMedicineDetails(patientId, medicineId, request);
        return ResponseEntity.ok(medicineService.getPatientMedicineById(patientId, medicineId));
    }
    @DeleteMapping ("{patientId}/medicines/{medicineId}")
    public ResponseEntity<String> deleteMedicine(@PathVariable("patientId") Integer patientId,
                                                 @PathVariable("medicineId") Integer medicineId){
        medicineService.deletePatientMedicineById(patientId, medicineId);
        return ResponseEntity.ok(MEDICATION_DELETED_MESSAGE);
    }
}
