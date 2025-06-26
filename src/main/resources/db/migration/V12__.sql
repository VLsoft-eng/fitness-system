-- Создание промежуточной таблицы для связи ManyToMany между training_sessions и full_exercises
CREATE TABLE training_session_full_exercise
(
    training_session_id BIGINT NOT NULL,
    full_exercise_id    BIGINT NOT NULL,
    PRIMARY KEY (training_session_id, full_exercise_id),
    CONSTRAINT fk_training_session FOREIGN KEY (training_session_id) REFERENCES training_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_full_exercise FOREIGN KEY (full_exercise_id) REFERENCES full_exercises (id) ON DELETE CASCADE
);

-- Перенос существующих связей из full_exercises.training_session_id в новую таблицу
INSERT INTO training_session_full_exercise (training_session_id, full_exercise_id)
SELECT training_session_id, id
FROM full_exercises
WHERE training_session_id IS NOT NULL;

-- Удаление столбца training_session_id из таблицы full_exercises
ALTER TABLE full_exercises
DROP
COLUMN training_session_id;