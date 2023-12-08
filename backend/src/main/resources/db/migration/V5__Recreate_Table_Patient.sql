-- Create a sequence starting from 1
DROP SEQUENCE IF EXISTS medicine_id_seq;
CREATE SEQUENCE medicine_id_seq START 1;

-- Create the medicine table
DROP TABLE medicine;
CREATE TABLE medicine
(
    id BIGINT DEFAULT nextval('medicine_id_seq') PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL,
    active_ingredient VARCHAR(255),
    times_daily INT NOT NULL,
    instructions VARCHAR(255) NOT NULL,
    interactions TEXT[],
    patient_id INT REFERENCES patients(id) ON DELETE CASCADE
);

-- Set the current value of the sequence to 1
SELECT setval('medicine_id_seq', 1);