CREATE TABLE gym_rooms
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    longitude   DOUBLE PRECISION NOT NULL,
    latitude    DOUBLE PRECISION NOT NULL,
    capacity    BIGINT       NOT NULL,
    base64_image TEXT
);