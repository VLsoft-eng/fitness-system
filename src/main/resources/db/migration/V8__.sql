CREATE TABLE approaches
(
    id                           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    approaches_count             NUMERIC(19, 2) NOT NULL,
    repetition_per_aproach_count NUMERIC(19, 2) NOT NULL,
    trainer_id                   BIGINT         NOT NULL,
    CONSTRAINT fk_approaches_trainer FOREIGN KEY (trainer_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);
