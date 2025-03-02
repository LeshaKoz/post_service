ALTER TABLE comment
    ADD COLUMN verified boolean DEFAULT false NOT NULL,
    ADD COLUMN verified_date timestamptz;