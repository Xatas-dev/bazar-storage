ALTER TABLE storage_node ADD COLUMN IF NOT EXISTS upload_status VARCHAR;

UPDATE storage_node
SET upload_status =
    CASE
        WHEN type = 'FOLDER' THEN NULL
        WHEN status = 'ERROR' THEN 'ERROR'
        WHEN status = 'DELETED' THEN 'UPLOADED'
        WHEN status = 'IN_PROGRESS' THEN 'IN_PROGRESS'
        ELSE 'UPLOADED'
    END
WHERE upload_status IS NULL;

UPDATE storage_node
SET status =
    CASE
        WHEN status = 'DELETED' THEN 'DELETED'
        ELSE 'ACTIVE'
    END