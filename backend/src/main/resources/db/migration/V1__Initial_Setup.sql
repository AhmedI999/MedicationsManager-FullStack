create table patients
(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255),
    age INT
);
create table medicine
(
    id BIGSERIAL PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL,
    active_ingredient VARCHAR(255),
    times_daily INT NOT NULL,
    instructions VARCHAR(255)   NOT NULL,
    interactions TEXT[],
    patient_id INT REFERENCES patients(id) ON DELETE CASCADE
);
