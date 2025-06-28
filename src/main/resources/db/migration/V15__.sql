-- Создание таблицы подписок
CREATE TABLE subscription (
                              id BIGINT PRIMARY KEY,
                              subscriber_id BIGINT UNIQUE,
                              personal_training_count BIGINT NOT NULL DEFAULT 0,
                              is_active BOOLEAN NOT NULL DEFAULT FALSE,
                              start_date DATE,
                              end_date DATE,
                              CONSTRAINT fk_subscription_user
                                  FOREIGN KEY (subscriber_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE
);

-- Добавление колонки подписки в таблицу пользователей
ALTER TABLE users
    ADD COLUMN subscription_id BIGINT UNIQUE;  -- пока nullable, чтобы обновить значения

-- Добавление внешнего ключа из users в subscription
ALTER TABLE users
    ADD CONSTRAINT fk_user_subscription
        FOREIGN KEY (subscription_id)
            REFERENCES subscription(id)
            ON DELETE CASCADE;

-- Создание sequence для генерации id подписок (если её ещё нет)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'subscription_id_seq') THEN
CREATE SEQUENCE subscription_id_seq START 1;
END IF;
END$$;

-- Вставка подписок для пользователей, у которых подписки нет
INSERT INTO subscription (id, subscriber_id, personal_training_count, is_active, start_date, end_date)
SELECT nextval('subscription_id_seq'), u.id, 0, FALSE, NULL, NULL
FROM users u
         LEFT JOIN subscription s ON s.subscriber_id = u.id
WHERE s.subscriber_id IS NULL;

-- Обновление поля subscription_id в users по созданным подпискам
UPDATE users u
SET subscription_id = s.id
    FROM subscription s
WHERE s.subscriber_id = u.id;