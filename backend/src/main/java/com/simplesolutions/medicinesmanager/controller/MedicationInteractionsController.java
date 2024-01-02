package com.simplesolutions.medicinesmanager.controller;

import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.dto.interactiondto.MedicationInteractionDTO;
import com.simplesolutions.medicinesmanager.service.medicine.MedicineService;
import com.simplesolutions.medicinesmanager.service.medicine.interactions.InteractionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class MedicationInteractionsController {
    private final MedicineService medicineService;
    private final InteractionsService interactionsService;

    @GetMapping("{patientId}/medicines/{medicineId}/interactions/{name}")
    public ResponseEntity<MedicationInteractionDTO> getMedicationInteraction(@PathVariable("patientId") Integer patientId,
                                                                             @PathVariable("medicineId") Integer medicineId,
                                                                             @PathVariable("name") String name) {
        MedicationInteractions interaction = interactionsService.getMedicineInteractionByName(medicineId, name);
        return ResponseEntity.ok(new MedicationInteractionDTO(interaction.getName(), interaction.getType()));
    }
    @GetMapping("{patientId}/medicines/{medicineId}/interactions")
    public List<MedicationInteractions> getAllMedicationInteractions(@PathVariable("patientId") Integer patientId,
                                                                     @PathVariable("medicineId") Integer medicineId){
        return interactionsService.getMedicineInteractions(patientId, medicineId);
    }
    @PostMapping("{patientId}/medicines/{medicineId}/interactions")
    public ResponseEntity<String> saveMedicationInteraction(@PathVariable("patientId") Integer patientId,
                                                                              @PathVariable("medicineId") Integer medicineId,
                                                                              @RequestBody MedicationInteractionDTO request) {
        Medication medication = medicineService.getPatientMedicineEntityById(patientId, medicineId);
        interactionsService.saveMedicineInteraction(request, medication);
        return ResponseEntity.ok("The new Interaction Saved Successfully");
    }
    @DeleteMapping("{patientId}/medicines/{medicineId}/interactions/{name}")
    public ResponseEntity<String> deleteMedicationInteraction(@PathVariable("patientId") Integer patientId,
                                                              @PathVariable("medicineId") Integer medicineId,
                                                              @PathVariable("name") String name){

        interactionsService.deleteMedicationInteractionByName(medicineId, name);
        return ResponseEntity.ok("Interaction deleted Successfully");
    }


}
