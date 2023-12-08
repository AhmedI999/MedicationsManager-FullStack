ALTER TABLE patients
    ADD CONSTRAINT patient_email_unique UNIQUE (email);
