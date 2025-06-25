CREATE TABLE full_exercises
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    exercise_id BIGINT NOT NULL,
    approach_id BIGINT NOT NULL,
    CONSTRAINT fk_full_exercises_exercise FOREIGN KEY (exercise_id)
        REFERENCES exercises (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_full_exercises_approach FOREIGN KEY (approach_id)
        REFERENCES approaches (id)
        ON DELETE CASCADE
);
