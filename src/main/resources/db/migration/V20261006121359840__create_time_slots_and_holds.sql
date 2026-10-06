CREATE TABLE time_slots (
    id VARCHAR(36) PRIMARY KEY,
    master_id VARCHAR(64) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_time_slots_master_status ON time_slots(master_id, status);

CREATE TABLE slot_holds (
    id VARCHAR(36) PRIMARY KEY,
    slot_id VARCHAR(36) NOT NULL,
    held_by VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_slot_holds_slot FOREIGN KEY (slot_id) REFERENCES time_slots(id) ON DELETE CASCADE
);

CREATE INDEX idx_slot_holds_slot ON slot_holds(slot_id);

CREATE TABLE slot_status_history (
    id VARCHAR(36) PRIMARY KEY,
    slot_id VARCHAR(36) NOT NULL,
    previous_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_slot_history_slot FOREIGN KEY (slot_id) REFERENCES time_slots(id) ON DELETE CASCADE
);

CREATE INDEX idx_slot_history_slot ON slot_status_history(slot_id);
