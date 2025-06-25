CREATE TABLE training_sessions
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    description        TEXT,
    type               VARCHAR(50)  NOT NULL,
    trainer_id         BIGINT,
    start_time         TIMESTAMP    NOT NULL,
    end_time           TIMESTAMP    NOT NULL,
    duration_minutes   INTEGER      NOT NULL,
    max_participants   INTEGER,
    current_participants INTEGER DEFAULT 0 NOT NULL,
    location           VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP,

    CONSTRAINT fk_trainer FOREIGN KEY (trainer_id) REFERENCES users (id)
);

CREATE INDEX idx_training_sessions_trainer_id ON training_sessions (trainer_id);
CREATE INDEX idx_training_sessions_start_time ON training_sessions (start_time);