package com.simplesolutions.medicinesmanager.service.medicine;

import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.paylod.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.paylod.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.service.patient.PatientDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MedicineService {
    private final PatientDao patientDao;
    private final MedicineDao medicineDao;

    public List<Medicine> getPatientMedicines(Integer patientID){
        Patient patient = patientDao.selectPatientById(patientID).
        orElseThrow(() -> new ResourceNotFoundException("Patient doesn't exist"));
        if (patient.getPatientMedicines() == null || patient.getPatientMedicines().isEmpty())
            throw new ResourceNotFoundException("Patient doesn't have medicines");
        return medicineDao.selectPatientMedicines(patient.getId());
    }
    public Medicine getPatientMedicineById(Integer patientId, Integer medicineId){
        return medicineDao.selectPatientMedicineById(patientId, medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine wasn't found"));
    }
    public void deletePatientMedicineById(Integer patientId, Integer medicineId){
        medicineDao.deletePatientMedicineById(patientId, medicineId);
    }
    public boolean doesMedicineExists(String email, String brandName){
        return medicineDao.doesPatientMedicineExists(email, brandName);
    }
    public void savePatientMedicine(MedicineRegistrationRequest request, Patient patient){
        if (doesMedicineExists(patient.getEmail(), request.getBrandName()))
            throw new DuplicateResourceException("Patient's medicine (%s) already Exists"
                    .formatted(request.getBrandName()));
        Medicine medicine =  Medicine.builder()
                .brandName(request.getBrandName())
                .activeIngredient(request.getActiveIngredient())
                .timesDaily(request.getTimesDaily())
                .instructions(request.getInstructions())
                .interactions(request.getInteractions())
                .build();
        if (!patientDao.doesPatientExists(patient.getEmail()))
            throw new ResourceNotFoundException("Patient doesn't exist");
        medicine.setPatient(patient);
        medicineDao.saveMedicine(medicine);
    }
    public void editMedicineDetails(Integer patientId,Integer medicineId, MedicineUpdateRequest request){
        Medicine medicine = medicineDao.selectPatientMedicineById(patientId, medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine doesn't exist"));
        Patient patient = patientDao.selectPatientById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient doesn't exist"));
        boolean changes = false;
        if (request.getBrandName() != null && !request.getBrandName().equals(medicine.getBrandName())) {
            if (medicineDao.doesPatientMedicineExists(patient.getEmail() , request.getBrandName())) {
                throw new DuplicateResourceException(
                        "Brand name already taken"
                );
            }
            medicine.setBrandName(request.getBrandName());
            changes = true;
        }
        if (request.getActiveIngredient() != null && !request.getActiveIngredient().equals(medicine.getActiveIngredient())) {
            medicine.setActiveIngredient(request.getActiveIngredient());
            changes = true;
        }
        if (request.getTimesDaily() != null && !request.getTimesDaily().equals(medicine.getTimesDaily())) {
            medicine.setTimesDaily(request.getTimesDaily());
            changes = true;
        }
        if (request.getInteractions() != null && !request.getInteractions().equals(medicine.getInteractions())) {
            medicine.setInteractions(request.getInteractions());
            changes = true;
        }
        if (request.getInstructions() != null && !request.getInstructions().equals(medicine.getInstructions())) {
            medicine.setInstructions(request.getInstructions());
            changes = true;
        }
       if (!changes) {
            throw new UpdateException("no data changes found");
        }
        medicineDao.updateMedicine(medicine);


    }
}
