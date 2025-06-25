CREATE TABLE exercises
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    trainer_id  BIGINT       NOT NULL,
    CONSTRAINT fk_exercises_trainer FOREIGN KEY (trainer_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);
