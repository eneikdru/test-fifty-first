CREATE TABLE professionals (
    id BIGSERIAL PRIMARY KEY,
    facebook_id VARCHAR(255),
    phone_number VARCHAR(50),
    full_name VARCHAR(255) NOT NULL,
    description TEXT,
    address TEXT,
    avatar_url VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_professionals_facebook_id UNIQUE (facebook_id),
    CONSTRAINT uq_professionals_phone_number UNIQUE (phone_number)
);

CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    professional_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price_gel NUMERIC(10, 2) NOT NULL,
    duration_minutes INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_services_professional FOREIGN KEY (professional_id) REFERENCES professionals(id) ON DELETE CASCADE
);

CREATE INDEX idx_services_professional_id ON services(professional_id);
