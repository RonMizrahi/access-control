-- Clean tables before inserting
DELETE FROM user_roles;
DELETE FROM users;

-- Predefined users for H2 database
-- Passwords are bcrypt-hashed for 'password'
INSERT INTO USERS (id, username, password, subscription_plan) VALUES (1, 'admin1', 'password1', 'FREE');
INSERT INTO USERS (id, username, password, subscription_plan) VALUES (2, 'admin2', 'password2', 'FREE');
INSERT INTO USERS (id, username, password, subscription_plan) VALUES (3, 'user1', 'password3', 'BASIC');
INSERT INTO USERS (id, username, password, subscription_plan) VALUES (4, 'user2', 'password14', 'BASIC');

-- Assign roles
INSERT INTO USER_ROLES (user_id, roles) VALUES (1, 'ADMIN');
INSERT INTO USER_ROLES (user_id, roles) VALUES (2, 'ADMIN');
INSERT INTO USER_ROLES (user_id, roles) VALUES (3, 'USER');
INSERT INTO USER_ROLES (user_id, roles) VALUES (4, 'USER');
