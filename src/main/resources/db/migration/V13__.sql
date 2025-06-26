-- Удаление всех записей из промежуточной таблицы training_session_full_exercise
DELETE FROM training_session_full_exercise;

-- Удаление всех записей из таблицы full_exercises
DELETE FROM full_exercises;

-- Добавление столбца trainer_id в таблицу full_exercises
ALTER TABLE full_exercises
    ADD COLUMN trainer_id BIGINT NOT NULL;

-- Добавление внешнего ключа для trainer_id
ALTER TABLE full_exercises
    ADD CONSTRAINT fk_full_exercise_trainer
        FOREIGN KEY (trainer_id) REFERENCES users(id) ON DELETE RESTRICT;