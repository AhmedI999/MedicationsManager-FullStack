package com.simplesolutions.medicinesmanager.service.medicine;

import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.model.Medicine;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.paylod.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.paylod.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.service.patient.PatientDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class MedicineService {
    private final PatientDao patientDao;
    private final MedicineDao medicineDao;
    @Value("#{'${medicine.picture-url}'}")
    private String DEFAULT_PICTURE_URL;

    public List<Medicine> getPatientMedicines(Integer patientID){
        Patient patient = patientDao.selectPatientById(patientID).
        orElseThrow(() -> new ResourceNotFoundException("Patient doesn't exist"));
        if (patient.getPatientMedicines() == null || patient.getPatientMedicines().isEmpty())
            throw new ResourceNotFoundException("Patient doesn't have medicines");
        List<Medicine> medicines =  medicineDao.selectPatientMedicines(patient.getId());
        medicines.sort(Comparator.comparing(Medicine::getMedicineNumber));
        return medicines;
    }
    public Medicine getPatientMedicineById(Integer patientId, Integer medicineId){
        return medicineDao.selectPatientMedicineById(patientId, medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine wasn't found"));
    }
    public Medicine getPatientMedicineByBrandName (Integer patientId, String brandName){
        String capitalizedName = brandName.substring(0, 1).toUpperCase() +
                brandName.substring(1).toLowerCase();
        Medicine medicine = medicineDao.selectPatientMedicineByBrandName(patientId, capitalizedName);
        if (Objects.isNull(medicine)){
            throw new ResourceNotFoundException("Medicine %s wasn't found".formatted(capitalizedName));
        } else return medicine;
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
        String capitalizedName = request.getBrandName().substring(0, 1).toUpperCase() +
                request.getBrandName().substring(1).toLowerCase();
        Medicine medicine =  Medicine.builder()
                .pictureUrl(request.getPictureUrl())
                .brandName(capitalizedName)
                .activeIngredient(request.getActiveIngredient())
                .timesDaily(request.getTimesDaily())
                .instructions(request.getInstructions())
                .build();
        if (!patientDao.doesPatientExists(patient.getEmail()))
            throw new ResourceNotFoundException("Patient doesn't exist");
        if (request.getPictureUrl() == null || request.getPictureUrl().isEmpty())
            medicine.setPictureUrl(DEFAULT_PICTURE_URL);
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
        if (request.getPictureUrl() != null && !request.getPictureUrl().equals(medicine.getPictureUrl())) {
            medicine.setPictureUrl(request.getPictureUrl());
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
