package com.simplesolutions.medicinesmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "medicine")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "medicine_number")
    Integer medicineNumber;
    @Column(name = "picture_url")
    String pictureUrl;
    @Column(name = "brand_name")
    String brandName;
    @Column(name = "active_ingredient")
    String activeIngredient;
    @Column(name = "times_daily")
    Integer timesDaily;
    @Column(name = "instructions")
    String instructions;
    @JsonManagedReference
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<MedicationInteractions> interactions;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnore
    Patient patient;
}
