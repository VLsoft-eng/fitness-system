ALTER TABLE full_exercises
    ADD COLUMN training_session_id BIGINT;

ALTER TABLE full_exercises
    ADD CONSTRAINT fk_full_exercises_training_session
        FOREIGN KEY (training_session_id)
            REFERENCES training_sessions (id)
            ON DELETE SET NULL;
