package com.simplesolutions.medicinesmanager.service.medicine;

import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicationResponseDTO;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicationDTOMapper;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineRegistrationRequest;
import com.simplesolutions.medicinesmanager.dto.medicationsdto.MedicineUpdateRequest;
import com.simplesolutions.medicinesmanager.exception.DuplicateResourceException;
import com.simplesolutions.medicinesmanager.exception.ResourceNotFoundException;
import com.simplesolutions.medicinesmanager.exception.UpdateException;
import com.simplesolutions.medicinesmanager.model.Medication;
import com.simplesolutions.medicinesmanager.model.Patient;
import com.simplesolutions.medicinesmanager.service.patient.PatientDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MedicineService {
    private final PatientDao patientDao;
    private final MedicineDao medicineDao;
    private final MedicationDTOMapper medicationDTOMapper;
    @Value("#{'${medicine.picture-url}'}")
    private String DEFAULT_PICTURE_URL;
    @Value("#{'${service.patient.not-found-general.message}'}")
    private String PATIENT_NOT_EXISTS;
    @Value("#{'${service.patient-medications.list-empty.message}'}")
    private String PATIENT_MEDICATIONS_EMPTY_MSG;
    @Value("#{'${service.patient-medications.not-found-id.message}'}")
    private String PATIENT_MEDICATION_NOT_FOUND_ID_MSG;
    @Value("#{'${service.patient-medications.not-found-name.message}'}")
    private String PATIENT_MEDICATION_NOT_FOUND_NAME_MSG;
    @Value("#{'${service.patient-medications.already-exists.message}'}")
    private String PATIENT_MEDICATION_ALREADY_EXISTS_MSG;
    @Value("#{'${service.patient.not-found-general.message}'}")
    private String PATIENT_NOT_FOUND_MSG;
    @Value("#{'${service.patient-medications.not-found-general.message}'}")
    private String MEDICATION_NOT_FOUND_MSG;
    @Value("#{'${service.patient-medications.name-taken.message}'}")
    private String PATIENT_MEDICATION_ALREADY_EXISTS_NAME_MSG;
    @Value("#{'${service.patient-medications.no-details-changes.message}'}")
    private String PATIENT_MEDICATION_UPDATE_NO_CHANGES_MSG;


    public List<MedicationResponseDTO> getPatientMedicines(Integer patientID){
        Patient patient = patientDao.selectPatientById(patientID).
        orElseThrow(() -> new ResourceNotFoundException(PATIENT_NOT_EXISTS));
        List<Medication> medications =  medicineDao.selectPatientMedicines(patient.getId());
        if ( patient.getPatientMedications() == null ) {
            throw new ResourceNotFoundException(PATIENT_MEDICATIONS_EMPTY_MSG);
        }
        return medications.stream()
                .map(medicationDTOMapper)
                .sorted(Comparator.comparing(MedicationResponseDTO::medicineNumber))
                .collect(Collectors.toList());

    }
    public MedicationResponseDTO getPatientMedicineById(Integer patientId, Integer medicineId){
        return medicineDao.selectPatientMedicineById(patientId, medicineId)
                .map(medicationDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT_MEDICATION_NOT_FOUND_ID_MSG.formatted(medicineId)));
    }
    public Medication getPatientMedicineEntityById (Integer patientId, Integer medicineId){
        return medicineDao.selectPatientMedicineById(patientId, medicineId)
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT_MEDICATION_NOT_FOUND_ID_MSG.formatted(medicineId)));
    }
    public MedicationResponseDTO getPatientMedicineByBrandName (Integer patientId, String brandName){
        String capitalizedName = brandName.substring(0, 1).toUpperCase() +
                brandName.substring(1).toLowerCase();
        return medicineDao.selectPatientMedicineByBrandName(patientId, capitalizedName)
                .map(medicationDTOMapper)
                .orElseThrow(
                        () -> new ResourceNotFoundException(PATIENT_MEDICATION_NOT_FOUND_NAME_MSG.formatted(capitalizedName)));
    }
    public void deletePatientMedicineById(Integer patientId, Integer medicineId){
        medicineDao.deletePatientMedicineById(patientId, medicineId);
    }
    public boolean doesMedicineExists(String email, String brandName){
        return medicineDao.doesPatientMedicineExists(email, brandName);
    }
    public void savePatientMedicine(MedicineRegistrationRequest request, Patient patient){
        if (doesMedicineExists(patient.getEmail(), request.brandName()))
            throw new DuplicateResourceException(PATIENT_MEDICATION_ALREADY_EXISTS_MSG
                    .formatted(request.brandName()));
        String capitalizedName = request.brandName().substring(0, 1).toUpperCase() +
                request.brandName().substring(1).toLowerCase();
        Medication medication = Medication.builder()
                .pictureUrl(request.pictureUrl())
                .brandName(capitalizedName)
                .activeIngredient(request.activeIngredient())
                .timesDaily(request.timesDaily())
                .instructions(request.instructions())
                .build();
        if (!patientDao.doesPatientExists(patient.getEmail()))
            throw new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG);
        if (request.pictureUrl() == null || request.pictureUrl().isEmpty())
            medication.setPictureUrl(DEFAULT_PICTURE_URL);
        medication.setPatient(patient);
        medicineDao.saveMedicine(medication);
    }
    public void editMedicineDetails(Integer patientId,Integer medicineId, MedicineUpdateRequest request){
        Patient patient = patientDao.selectPatientById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT_NOT_FOUND_MSG));
        Medication medication = medicineDao.selectPatientMedicineById(patientId, medicineId)
                .orElseThrow(() -> new ResourceNotFoundException(MEDICATION_NOT_FOUND_MSG));
        boolean changes = false;
        if (request.getBrandName() != null && !request.getBrandName().trim().equals(medication.getBrandName())) {
            if (medicineDao.doesPatientMedicineExists(patient.getEmail() , request.getBrandName())) {
                throw new DuplicateResourceException(
                        PATIENT_MEDICATION_ALREADY_EXISTS_NAME_MSG
                );
            }
            medication.setBrandName(request.getBrandName().trim());
            changes = true;
        }
        if (request.getPictureUrl() != null && !request.getPictureUrl().equals(medication.getPictureUrl())) {
            medication.setPictureUrl(request.getPictureUrl().trim());
            changes = true;
        }
        if (request.getActiveIngredient() != null && !request.getActiveIngredient().equals(medication.getActiveIngredient())) {
            medication.setActiveIngredient(request.getActiveIngredient().trim());
            changes = true;
        }
        if (request.getTimesDaily() != null && !request.getTimesDaily().equals(medication.getTimesDaily())) {
            medication.setTimesDaily(request.getTimesDaily());
            changes = true;
        }
        if (request.getInstructions() != null && !request.getInstructions().equals(medication.getInstructions())) {
            medication.setInstructions(request.getInstructions().trim());
            changes = true;
        }
       if (!changes) {
            throw new UpdateException(PATIENT_MEDICATION_UPDATE_NO_CHANGES_MSG);
        }
        medicineDao.updateMedicine(medication);
    }
}
