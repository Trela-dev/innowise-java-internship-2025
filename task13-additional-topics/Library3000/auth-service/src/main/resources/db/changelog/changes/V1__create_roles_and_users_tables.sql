CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,  -- BCRYPT length
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);


INSERT INTO roles (name) VALUES
('ROLE_USER'),
('ROLE_ADMIN');


-- Insert admin user with bcrypt-hashed password (admin / admin)
INSERT INTO users (username, password, role_id)
VALUES (
    'admin',
    '$2a$10$OLM9qB4EzvRFweTXvO3KPOBJvEM9w44vRbDUhdJUeGCaN6RN0nTEG',
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
);

-- Insert multiple user entries with bcrypt-hashed passwords
-- User1: user1
-- User2: user2
-- User3: user3
-- User4: user4
-- User5: user5
INSERT INTO users (username, password, role_id)
VALUES
    ('user1', '$2a$10$/8T/vclN9f8QKCCZlXGSlOFdPuhadH/cO8eU/OPZR/hij3Q3a.GEu', (SELECT id FROM roles WHERE name = 'ROLE_USER')),
    ('user2', '$2a$10$nmVJt7/5859qeSGeMyybKu5y07L9aPJx5mPJEgMkaRH4ZC2oCgoIq', (SELECT id FROM roles WHERE name = 'ROLE_USER')),
    ('user3', '$2a$10$qAze5/SipvRnFhYrYpKL4eDyAK/damnARaB8/s5LZXxPmJrG.3o7a', (SELECT id FROM roles WHERE name = 'ROLE_USER')),
    ('user4', '$2a$10$NeEikwPL8fcMNtKRFNPH/OAnUDUkoODcsni2ppmM5jHlp1ha.dDcy', (SELECT id FROM roles WHERE name = 'ROLE_USER')),
    ('user5', '$2a$10$gVZJ3qIuQGhxkBzqsQ5k4.Kl5NV8fgAmuXA1qTthzIbrVmorvgdKS', (SELECT id FROM roles WHERE name = 'ROLE_USER'));
