CREATE TABLE IF NOT EXISTS album_allowed_users (
    id bigserial PRIMARY KEY,
    album_id bigint not null,
    user_id bigint not null,

    CONSTRAINT fk_album_user_id FOREIGN KEY (album_id) REFERENCES album (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS album_allowed_users_album_id_user_id_unique
ON album_allowed_users (album_id, user_id);