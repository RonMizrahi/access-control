-- PostgreSQL initialization script
-- This script runs when the PostgreSQL container starts for the first time

-- Create database if it doesn't exist (handled by POSTGRES_DB environment variable)

-- Create additional schemas if needed
-- CREATE SCHEMA IF NOT EXISTS app_schema;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE springdb TO spring;

-- Create extensions if needed
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- You can add additional initialization here
-- CREATE TABLE IF NOT EXISTS sample_table (
--     id SERIAL PRIMARY KEY,
--     name VARCHAR(255) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- Insert sample data if needed
-- INSERT INTO sample_table (name) VALUES ('Sample Record 1'), ('Sample Record 2');
