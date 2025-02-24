CREATE UNIQUE INDEX IF NOT EXISTS favorite_album_album_id_user_id_key
ON favorite_albums (album_id, user_id);