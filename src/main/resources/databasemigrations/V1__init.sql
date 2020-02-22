CREATE TABLE users (
    id uuid NOT NULL PRIMARY KEY,
    email text NOT NULL,
    hashed_password text NOT NULL,
    salt text NOT NULL,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
