package com.simplesolutions.medicinesmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simplesolutions.medicinesmanager.utils.StringListConverter;
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
    int id;
    @Column(name = "brand_name")
    String brandName;
    @Column(name = "active_ingredient")
    String activeIngredient;
    @Column(name = "times_daily")
    Integer timesDaily;
    @Column(name = "instructions")
    String instructions;
    @Convert(converter = StringListConverter.class)
    @Column(name = "interactions")
    List<String> interactions;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnore
    Patient patient;
}
