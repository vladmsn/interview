-- indexes to improve query performance on frequently searched columns

CREATE INDEX IF NOT EXISTS idx_inspection_vin
    ON inspection (vin);

CREATE INDEX idx_inspection_active_vin_created_at
    ON inspection (is_deleted, vin, created_at DESC);

CREATE INDEX idx_item_by_inspection_active_created_at
    ON inspection_item (inspection_id, is_deleted, created_at DESC);
