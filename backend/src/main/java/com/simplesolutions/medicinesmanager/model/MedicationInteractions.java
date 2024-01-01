package com.simplesolutions.medicinesmanager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "medication_interactions")
public class MedicationInteractions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;
    @Column(name = "name")
    String name;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    InteractionType Type;
    @ManyToOne
    @JoinColumn(name = "medicine_id")
    @JsonBackReference
    Medication medicine;
}
