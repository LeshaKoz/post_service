ALTER TABLE post
    ADD COLUMN verified_date TIMESTAMP,
    ADD COLUMN verified boolean DEFAULT false NOT NULL;