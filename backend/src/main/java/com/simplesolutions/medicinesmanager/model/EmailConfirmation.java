package com.simplesolutions.medicinesmanager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "email_confirmation")
public class EmailConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;
    @Column(name = "token")
    String token;
    @Column(name = "created_at")
    LocalDateTime createdAt;
    @Column(name = "expires_at")
    LocalDateTime expiresAt;
    @Column(name = "confirmed_at")
    LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonBackReference
    @JsonIgnore
    Patient patient;

}
