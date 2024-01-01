package com.simplesolutions.medicinesmanager.repository;

import com.github.javafaker.Faker;
import com.simplesolutions.medicinesmanager.AbstractTestContainers;
import com.simplesolutions.medicinesmanager.model.InteractionType;
import com.simplesolutions.medicinesmanager.model.MedicationInteractions;
import com.simplesolutions.medicinesmanager.model.Medication;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DisplayName("Test for custom MedicationInteraction Repository methods")
class MedicationInteractionRepositoryTest extends AbstractTestContainers {
    @Autowired
    MedicineRepository medicineTest;
    @Autowired
    MedicationInteractionRepository interactionTest;
    Medication medication;
    @Value("#{'${medicine.picture-url}'}")
    String DEFAULT_PICTURE_URL;
    MedicationInteractions interaction;

    @BeforeEach
    void setUp() {
        Faker faker = new Faker();
        interaction = MedicationInteractions.builder()
                .name("U" + faker.lorem().word())
                .Type(InteractionType.MILD)
                .medicine(medication)
                .build();
        medication = Medication.builder()
                .pictureUrl(DEFAULT_PICTURE_URL)
                .brandName(faker.lorem().characters(5))
                .activeIngredient(faker.lorem().characters(10))
                .timesDaily(faker.random().nextInt(1,5))
                .instructions(faker.lorem().characters())
                .interactions(Collections.singletonList(interaction))
                .build();
        interaction.setMedicine(medication);

    }
    @Nested
    @DisplayName("For findByMedicineIdAndName method")
    class MedicationInteractionRepository_findByMedicationIdAndName {

        @Test
        @DisplayName("findByMedicineIdAndName returns interaction with valid name")
        void findByMedicineIdAndName_returnsMedicineInteraction() {
            // Given
            medicineTest.save(medication);
            interactionTest.save(interaction);
            //When
            System.out.println("after saving");
            MedicationInteractions actualInteraction = interactionTest
                    .findByMedicineIdAndName(medication.getId(), interaction.getName());
            //Then
            assertThat(actualInteraction)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(interaction);
        }

        @Test
        @DisplayName("findByMedicineIdAndName throws Not found exception when invalid name is used")
        void findByMedicineIdAndName_throwsNotfound() {
            // Given
            medicineTest.save(medication);
            String invalidInteractionName = "InValid Interaction Name";
            //When
            MedicationInteractions actualInteraction = interactionTest
                    .findByMedicineIdAndName(medication.getId(), invalidInteractionName);
            //Then
            assertThat(actualInteraction).isNull();
        }
    }
    @Nested
    @DisplayName("For existsByMedicineIdAndName method")
    class MedicationInteractionRepository_existsByMedicationIdAndName {
        @Test
        @DisplayName("Returns If An interaction exists Using a valid name")
        void existsByMedicineIdAndName_returnsTrue() {
            // Given
            medicineTest.save(medication);
            interactionTest.save(interaction);
            //When
            boolean actual = interactionTest
                    .existsByMedicineIdAndName(medication.getId(), interaction.getName());
            //Then
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Returns If An interaction Doesn't exists Using an invalid name")
        void existsByMedicineIdAndName_returnsFalse() {
            // Given
            medicineTest.save(medication);
            String invalidName = "Invalid Interaction Name";
            //When
            boolean actual = interactionTest.existsByMedicineIdAndName(medication.getId(), invalidName);
            //Then
            assertThat(actual).isFalse();
        }
    }
}