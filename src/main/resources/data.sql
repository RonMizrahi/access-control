-- Predefined users for H2 database
-- Passwords are bcrypt-hashed for 'password'
INSERT INTO "USERS" (id, username, password, subscription_plan) VALUES (1, 'admin1', '$2a$10$7QJ8QwQwQwQwQwQwQwQwQeQwQwQwQwQwQwQwQwQwQwQwQwQwQwQw', 'FREE');
INSERT INTO "USERS" (id, username, password, subscription_plan) VALUES (2, 'admin2', '$2a$10$7QJ8QwQwQwQwQwQwQwQwQeQwQwQwQwQwQwQwQwQwQwQwQwQwQwQw', 'FREE');
INSERT INTO "USERS" (id, username, password, subscription_plan) VALUES (3, 'user1', '$2a$10$7QJ8QwQwQwQwQwQwQwQwQeQwQwQwQwQwQwQwQwQwQwQwQwQwQwQw', 'BASIC');
INSERT INTO "USERS" (id, username, password, subscription_plan) VALUES (4, 'user2', '$2a$10$7QJ8QwQwQwQwQwQwQwQwQeQwQwQwQwQwQwQwQwQwQwQwQwQwQwQw', 'BASIC');

-- Assign roles
INSERT INTO USER_ROLES (user_id, roles) VALUES (1, 'ADMIN');
INSERT INTO USER_ROLES (user_id, roles) VALUES (2, 'ADMIN');
INSERT INTO USER_ROLES (user_id, roles) VALUES (3, 'USER');
INSERT INTO USER_ROLES (user_id, roles) VALUES (4, 'USER');
