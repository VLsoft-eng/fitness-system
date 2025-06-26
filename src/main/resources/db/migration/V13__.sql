-- Добавление столбца trainer_id в таблицу full_exercises
ALTER TABLE full_exercises
    ADD COLUMN trainer_id BIGINT NOT NULL;

-- Заполнение trainer_id на основе trainer_id из таблицы exercises
UPDATE full_exercises fe
SET trainer_id = e.trainer_id
    FROM exercises e
WHERE fe.exercise_id = e.id;

-- Добавление внешнего ключа для trainer_id
ALTER TABLE full_exercises
    ADD CONSTRAINT fk_full_exercise_trainer
        FOREIGN KEY (trainer_id) REFERENCES users(id) ON DELETE RESTRICT;