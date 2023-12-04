package com.simplesolutions.medicinesmanager.paylod;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.simplesolutions.medicinesmanager.model.Medicine;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponse {
    final String email;
    final String firstname;
    final String lastname;
    final int age;
    final List<Medicine> patientMedicines;
}
