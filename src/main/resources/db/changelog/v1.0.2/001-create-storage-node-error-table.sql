CREATE TABLE storage_node_error (
    id BIGSERIAL PRIMARY KEY,
    node_id BIGINT NOT NULL REFERENCES storage_node,
    error_code VARCHAR NOT NULL,
    description VARCHAR,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);