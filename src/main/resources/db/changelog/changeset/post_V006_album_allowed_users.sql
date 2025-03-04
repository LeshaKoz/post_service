CREATE TABLE IF NOT EXISTS album_allowed_users(
    album_id bigint NOT NULL,
    user_id bigint NOT NULL,
    PRIMARY KEY (album_id, user_id),
    FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE
);