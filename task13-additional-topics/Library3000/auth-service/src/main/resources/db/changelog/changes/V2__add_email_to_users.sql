ALTER TABLE users
ADD COLUMN email VARCHAR(255) UNIQUE;

UPDATE users
SET email = username || '@example.com';

ALTER TABLE users
ALTER COLUMN email SET NOT NULL;