CREATE SEQUENCE medicine_number_seq;
create table medicine
(
    id SERIAL PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL,
    active_ingredient VARCHAR(255),
    times_daily INT NOT NULL,
    instructions VARCHAR(255)   NOT NULL,
    medicine_number INT DEFAULT nextval('medicine_number_seq'),
    patient_id INT REFERENCES patients(id) ON DELETE CASCADE
);
-- medicine_number is incremented automatically
CREATE OR REPLACE FUNCTION set_medicine_number()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.medicine_number = COALESCE(
                                  (SELECT MAX(medicine_number) FROM medicine WHERE patient_id = NEW.patient_id),
                                  0
                          ) + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_medicine_number_trigger
    BEFORE INSERT ON medicine
    FOR EACH ROW
EXECUTE FUNCTION set_medicine_number();