CREATE TABLE hashtag (
    name varchar(64) NOT NULL UNIQUE
);

CREATE TABLE post_hashtag (
    id bigserial PRIMARY KEY,
    post_id bigint not null,
    hashtag_name varchar(64) not null,

    CONSTRAINT fk_post_hashtag_id FOREIGN KEY (post_id) REFERENCES post (id),
    CONSTRAINT fk_hashtag_post_id FOREIGN KEY (hashtag_name) REFERENCES hashtag (name)
);