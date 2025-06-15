CREATE TABLE "user"
(
    id        BIGINT       NOT NULL,
    password  VARCHAR(255) NOT NULL,
    username  VARCHAR(255) NOT NULL,
    firstname VARCHAR(255),
    lastname  VARCHAR(255),
    gender    VARCHAR(255),
    birthday  date,
    role      VARCHAR(255),
    CONSTRAINT pk_user PRIMARY KEY (id)
);