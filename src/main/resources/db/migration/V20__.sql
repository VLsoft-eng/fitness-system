CREATE TABLE subscription_specific
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                    VARCHAR(255),
    description             TEXT,
    personal_training_count BIGINT NOT NULL DEFAULT 0,
    subscription_days_count BIGINT NOT NULL DEFAULT 0
);