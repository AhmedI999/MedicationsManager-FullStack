CREATE TABLE email_confirmation
(
    id              SERIAL PRIMARY KEY,
    token           VARCHAR(512) NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    expires_at      TIMESTAMP NOT NULL,
    confirmed_at    TIMESTAMP,
    patient_id INT REFERENCES patients(id) ON DELETE CASCADE
)