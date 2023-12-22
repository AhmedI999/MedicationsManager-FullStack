CREATE TABLE medication_interactions
(
    id                SERIAL PRIMARY KEY,
    name              VARCHAR(255),
    type              VARCHAR(255),
    medicine_id INT REFERENCES medicine(id) ON DELETE CASCADE
);
