-- Migration: Master Profiles, Availability Slots, and Bookings
-- Version: V20261006121412910

CREATE TABLE master_profiles (
    id VARCHAR(64) PRIMARY KEY,
    full_name VARCHAR(128) NOT NULL,
    city VARCHAR(64) NOT NULL,
    phone_number VARCHAR(32) NOT NULL,
    facebook_profile_url VARCHAR(256),
    bio TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_master_profiles_city ON master_profiles(city);

CREATE TABLE availability_slots (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    master_id VARCHAR(64) NOT NULL REFERENCES master_profiles(id) ON DELETE CASCADE,
    service_name VARCHAR(128) NOT NULL,
    price_gel DOUBLE PRECISION NOT NULL,
    duration_minutes INT NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    is_booked BOOLEAN DEFAULT FALSE NOT NULL,
    status VARCHAR(32) DEFAULT 'AVAILABLE' NOT NULL
);

CREATE INDEX idx_availability_slots_master_time ON availability_slots(master_id, start_time);
CREATE INDEX idx_availability_slots_status ON availability_slots(status);

CREATE TABLE bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_reference VARCHAR(64) NOT NULL UNIQUE,
    master_id VARCHAR(64) NOT NULL REFERENCES master_profiles(id) ON DELETE CASCADE,
    slot_id BIGINT NOT NULL REFERENCES availability_slots(id) ON DELETE CASCADE,
    customer_name VARCHAR(128) NOT NULL,
    customer_phone VARCHAR(32) NOT NULL,
    status VARCHAR(32) DEFAULT 'CONFIRMED' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_bookings_master_id ON bookings(master_id);
CREATE INDEX idx_bookings_status ON bookings(status);
