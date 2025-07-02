CREATE TABLE train_machine
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    description   TEXT,
    base64_image  TEXT,
    count         BIGINT       NOT NULL,
    gym_room_id   BIGINT       NOT NULL,
    CONSTRAINT fk_gym_room FOREIGN KEY (gym_room_id) REFERENCES gym_rooms (id) ON DELETE CASCADE
);