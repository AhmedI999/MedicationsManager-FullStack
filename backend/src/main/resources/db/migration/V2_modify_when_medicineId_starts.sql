-- Drop the default sequence created for medicine id
DROP SEQUENCE IF EXISTS medicine_id_seq;

-- Create a new sequence starting from 1
CREATE SEQUENCE medicine_id_seq START 1;

-- Set the default value of the id column in medicine to use the new sequence
ALTER TABLE medicine ALTER COLUMN id SET DEFAULT nextval('medicine_id_seq');

-- Set the current value of the sequence to the maximum existing id in the medicine table
SELECT setval('medicine_id_seq', COALESCE((SELECT MAX(id) FROM medicine), 1));