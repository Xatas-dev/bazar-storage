CREATE TABLE storage_node (
    id BIGSERIAL PRIMARY KEY,
    space_id BIGINT NOT NULL,
    node_name TEXT NOT NULL,
    file_uuid UUID,
    status VARCHAR,
    type VARCHAR NOT NULL,
    parent_id BIGINT REFERENCES storage_node(id),
    size BIGINT,
    user_id UUID NOT NULL,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);