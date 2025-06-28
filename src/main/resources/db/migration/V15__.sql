-- 1. Создание таблицы подписок
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

-- 2. Добавление колонки подписки в таблицу пользователей (сейчас nullable)
ALTER TABLE users
    ADD COLUMN subscription_id BIGINT UNIQUE;

-- 3. Добавление внешнего ключа (сначала добавим без NOT NULL на колонке)
ALTER TABLE users
    ADD CONSTRAINT fk_user_subscription
        FOREIGN KEY (subscription_id)
            REFERENCES subscription(id)
            ON DELETE CASCADE;

-- 4. Создание sequence для генерации ID подписки (если еще нет)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'subscription_id_seq') THEN
CREATE SEQUENCE subscription_id_seq START 1;
END IF;
END$$;

-- 5. Вставка подписок для пользователей без подписки
INSERT INTO subscription (id, subscriber_id, personal_training_count, is_active, start_date, end_date)
SELECT nextval('subscription_id_seq'), u.id, 0, FALSE, NULL, NULL
FROM users u
         LEFT JOIN subscription s ON s.subscriber_id = u.id
WHERE s.subscriber_id IS NULL;

-- 6. Обновление поля subscription_id у пользователей — привязываем к созданным подпискам
UPDATE users u
SET subscription_id = s.id
    FROM subscription s
WHERE s.subscriber_id = u.id;

-- 7. Сделать колонку subscription_id NOT NULL после заполнения
ALTER TABLE users
    ALTER COLUMN subscription_id SET NOT NULL;
