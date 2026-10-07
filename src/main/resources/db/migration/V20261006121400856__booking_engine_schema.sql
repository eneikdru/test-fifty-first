-- Migration: Booking Engine Schema for Holds and Confirmations
-- Version: V20261006121400856

CREATE TABLE bookings (
    id VARCHAR(64) PRIMARY KEY,
    slot_id BIGINT NOT NULL,
    master_id BIGINT NOT NULL,
    customer_phone VARCHAR(32) NOT NULL,
    service_name VARCHAR(128) NOT NULL,
    price_gel NUMERIC(10, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    hold_expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_bookings_slot_id ON bookings(slot_id);
CREATE INDEX idx_bookings_status ON bookings(status);
