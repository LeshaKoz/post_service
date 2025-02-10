  CREATE TABLE hashtag (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(64) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE post_to_hashtag (
    post_id bigint NULL,
    hashtag_id bigint NULL,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE SET NULL,
    CONSTRAINT fk_hashtag_id FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE SET NULL
);