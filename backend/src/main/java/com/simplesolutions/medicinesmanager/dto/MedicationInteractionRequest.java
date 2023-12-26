package com.simplesolutions.medicinesmanager.dto;

import com.simplesolutions.medicinesmanager.model.InteractionType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicationInteractionRequest {
    final String name;
    final InteractionType type;
}
