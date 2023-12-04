ALTER TABLE medicine
    ADD CONSTRAINT medicine_brand_name_per_patient_unique UNIQUE (patient_id, brand_name);
