CREATE TABLE subscription
(
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    follower_id bigint NOT NULL,
    followee_id bigint NOT NULL,
    created_at  timestamptz DEFAULT current_timestamp,
    updated_at  timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_follower_id FOREIGN KEY (follower_id) REFERENCES users (id),
    CONSTRAINT fk_followee_id FOREIGN KEY (followee_id) REFERENCES users (id)
);

INSERT INTO subscription(follower_id, followee_id)
VALUES (2, 1),
       (3, 1),
       (4, 1);
