-- Migration: Master Services Catalog
-- Version: V20261006121413000

CREATE TABLE master_services (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    master_id BIGINT NOT NULL REFERENCES masters(id) ON DELETE CASCADE,
    name_en VARCHAR(255),
    name_ka VARCHAR(255),
    name_ru VARCHAR(255),
    description_en TEXT,
    description_ka TEXT,
    description_ru TEXT,
    price_gel DOUBLE PRECISION NOT NULL,
    duration_minutes INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_master_services_master_id ON master_services(master_id);
