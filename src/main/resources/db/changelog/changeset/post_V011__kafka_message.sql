CREATE TABLE IF NOT EXISTS kafka_message(
    id bigserial PRIMARY KEY,
    message text NOT NULL,
    topic varchar(255) NOT NULL,
    status varchar(50) NOT NULL DEFAULT 'PENDING',
    attempts integer NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT status_check CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);

CREATE INDEX kafka_message_created_at_status_idx ON kafka_message(created_at, status);