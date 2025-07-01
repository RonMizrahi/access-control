-- Predefined users for H2 database
-- Passwords are bcrypt-hashed for 'password'
INSERT INTO USERS (username, password, subscription_plan) VALUES ('admin1', 'password1', 'FREE');
INSERT INTO USERS (username, password, subscription_plan) VALUES ('admin2', 'password2', 'FREE');
INSERT INTO USERS (username, password, subscription_plan) VALUES ('user1', 'password3', 'BASIC');
INSERT INTO USERS (username, password, subscription_plan) VALUES ('user2', 'password14', 'BASIC');

-- Assign roles (using auto-generated IDs)
INSERT INTO USER_ROLES (user_id, roles) VALUES (1, 'ADMIN');
INSERT INTO USER_ROLES (user_id, roles) VALUES (2, 'ADMIN');
INSERT INTO USER_ROLES (user_id, roles) VALUES (3, 'USER');
INSERT INTO USER_ROLES (user_id, roles) VALUES (4, 'USER');
