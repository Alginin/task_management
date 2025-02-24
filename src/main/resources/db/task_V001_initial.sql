CREATE TABLE tasks (
    id  UUID    PRIMARY KEY,
    title VARCHAR(64)   NOT NULL,
    description VARCHAR(1024),
    userId  BIGINT
)