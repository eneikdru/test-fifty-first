-- Migration: Telemetry and Georgian Masters Data Seeding Schema
-- Version: V20261006121412910

CREATE TABLE masters (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    city VARCHAR(64) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    facebook_page_id VARCHAR(128),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_masters_city ON masters(city);

CREATE TABLE availability_slots (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    master_id BIGINT NOT NULL REFERENCES masters(id) ON DELETE CASCADE,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_slots_master_time ON availability_slots(master_id, start_time);
