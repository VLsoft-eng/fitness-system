-- Создание таблицы подписок
CREATE TABLE subscription (
                              id BIGINT PRIMARY KEY,
                              subscriber_id BIGINT NOT NULL UNIQUE,
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
    ADD COLUMN subscription_id BIGINT NOT NULL UNIQUE;

-- Добавление внешнего ключа из users в subscription
ALTER TABLE users
    ADD CONSTRAINT fk_user_subscription
        FOREIGN KEY (subscription_id)
            REFERENCES subscription(id)
            ON DELETE CASCADE;
