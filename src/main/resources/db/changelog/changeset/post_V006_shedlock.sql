CREATE TABLE shedlock (
     name VARCHAR(64) PRIMARY KEY,
     lock_until TIMESTAMP(3),
     locked_at TIMESTAMP(3),
     locked_by VARCHAR(255)
);