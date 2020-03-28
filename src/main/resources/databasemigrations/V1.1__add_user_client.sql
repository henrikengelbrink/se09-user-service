CREATE TABLE user_clients (
    id uuid NOT NULL PRIMARY KEY,
    user_id uuid NOT NULL,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
