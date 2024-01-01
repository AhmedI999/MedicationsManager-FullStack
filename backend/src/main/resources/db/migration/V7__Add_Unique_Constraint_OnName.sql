ALTER TABLE medication_interactions
    ADD COLUMN patient_id INT REFERENCES patients(id) ON DELETE CASCADE;

-- Add a composite unique constraint
ALTER TABLE medication_interactions
    ADD CONSTRAINT unique_interaction_name_per_medicine
        UNIQUE (name, medicine_id, patient_id);