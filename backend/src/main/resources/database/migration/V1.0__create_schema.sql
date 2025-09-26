CREATE TABLE inspection (
    id             SERIAL     PRIMARY KEY,
    vin            VARCHAR(17)   NOT NULL,
    status         VARCHAR(16)   NOT NULL,
    note           VARCHAR(255),
    recommendation VARCHAR(255),
    estimated_cost DECIMAL(10, 2),
    created_at     TIMESTAMP     NOT NULL,
    updated_at     TIMESTAMP     NOT NULL,
    is_deleted     BOOLEAN  DEFAULT FALSE,

    CONSTRAINT chk_inspection_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED'))
);

CREATE TABLE inspection_item (
    id            SERIAL      PRIMARY KEY,
    inspection_id INTEGER       NOT NULL,
    category      VARCHAR(64)   NOT NULL,
    severity      VARCHAR(16)   NOT NULL,
    note          VARCHAR(255),
    created_at    TIMESTAMP     NOT NULL,
    updated_at    TIMESTAMP     NOT NULL,
    is_deleted    BOOLEAN  DEFAULT FALSE,

    CONSTRAINT fk_item_inspection FOREIGN KEY (inspection_id) REFERENCES inspection(id),
    CONSTRAINT chk_item_severity CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);