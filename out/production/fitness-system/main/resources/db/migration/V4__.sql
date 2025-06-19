CREATE TABLE posts
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    image_base64 TEXT,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP
);