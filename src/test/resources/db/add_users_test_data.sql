CREATE TABLE users
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    username   varchar(64) UNIQUE       NOT NULL,
    password   varchar(128)             NOT NULL,
    email      varchar(64) UNIQUE       NOT NULL,
    phone      varchar(32) UNIQUE,
    about_me   varchar(4096),
    active     boolean     DEFAULT true NOT NULL,
    city       varchar(64),
    country_id bigint                   NOT NULL,
    experience int,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

INSERT INTO users (username, email, phone, password, active, about_me, country_id, city, experience, created_at,
                   updated_at)
VALUES ('JohnDoe', 'johndoe@example.com', '1234567890', 'password1', true, 'About John Doe', 1, 'New York', 2,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('JaneSmith', 'janesmith@example.com', '0987654321', 'password2', true, 'About Jane Smith', 2, 'London', 4,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('MichaelJohnson', 'michaeljohnson@example.com', '1112223333', 'password3', true, 'About Michael Johnson', 1,
        'Sydney', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('EmilyDavis', 'emilydavis@example.com', '4445556666', 'password4', true, 'About Emily Davis', 3, 'Paris', 8,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('WilliamTaylor', 'williamtaylor@example.com', '7778889999', 'password5', true, 'About William Taylor', 2,
        'Toronto', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('OliviaAnderson', 'oliviaanderson@example.com', '0001112222', 'password6', true, 'About Olivia Anderson', 1,
        'Berlin', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('JamesWilson', 'jameswilson@example.com', '3334445555', 'password7', true, 'About James Wilson', 3, 'Tokyo', 14,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('SophiaMartin', 'sophiamartin@example.com', '6667778888', 'password8', true, 'About Sophia Martin', 4, 'Rome',
        16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('BenjaminThompson', 'benjaminthompson@example.com', '9990001111', 'password9', true, 'About Benjamin Thompson',
        4, 'Moscow', 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('AvaHarris', 'avaharris@example.com', '2223334444', 'password10', true, 'About Ava Harris', 3, 'Madrid', 20,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);