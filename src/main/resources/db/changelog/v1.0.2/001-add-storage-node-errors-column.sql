ALTER TABLE storage_node
    ADD COLUMN IF NOT EXISTS storage_node_errors JSONB NOT NULL DEFAULT '[]'::JSONB;