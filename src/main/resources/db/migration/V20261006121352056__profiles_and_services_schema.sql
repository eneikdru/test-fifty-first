-- Migration: Profiles and Services Schema
-- Version: V20261006121352056

CREATE TABLE master_profiles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    facebook_id VARCHAR(128),
    facebook_page_id VARCHAR(128),
    phone_number VARCHAR(32) NOT NULL,
    full_name VARCHAR(128) NOT NULL,
    city VARCHAR(64),
    address VARCHAR(256),
    description TEXT,
    public_profile_url VARCHAR(256),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_master_profiles_fb_id ON master_profiles(facebook_id);
CREATE INDEX idx_master_profiles_phone ON master_profiles(phone_number);

CREATE TABLE master_services (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    master_id BIGINT NOT NULL REFERENCES master_profiles(id) ON DELETE CASCADE,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    price_gel NUMERIC(10, 2) NOT NULL,
    duration_minutes INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_master_services_master_id ON master_services(master_id);
