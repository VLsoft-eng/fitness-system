CREATE TABLE enrollments
(
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT       NOT NULL,
    training_session_id BIGINT       NOT NULL,
    enrollment_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status              VARCHAR(50)  NOT NULL,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_training_session FOREIGN KEY (training_session_id) REFERENCES training_sessions (id),
    CONSTRAINT uq_user_session UNIQUE (user_id, training_session_id)
);

CREATE INDEX idx_enrollments_user_id ON enrollments (user_id);
CREATE INDEX idx_enrollments_training_session_id ON enrollments (training_session_id);
CREATE INDEX idx_enrollments_status ON enrollments (status);