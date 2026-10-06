-- Migration: Availability Slots and Hold Locks Schema
-- Version: V20261006121359840

CREATE TABLE availability_slots (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    master_id VARCHAR(128) NOT NULL,
    service_id VARCHAR(128),
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'FREE',
    held_by VARCHAR(128),
    held_until TIMESTAMP WITH TIME ZONE,
    booking_id VARCHAR(128),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_availability_slots_master_time ON availability_slots(master_id, start_time, end_time);
CREATE INDEX idx_availability_slots_status ON availability_slots(status);

CREATE TABLE slot_status_history (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    slot_id BIGINT NOT NULL REFERENCES availability_slots(id) ON DELETE CASCADE,
    previous_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    changed_by VARCHAR(128),
    reason VARCHAR(256),
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_slot_status_history_slot_time ON slot_status_history(slot_id, changed_at);
