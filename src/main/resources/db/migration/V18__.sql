ALTER TABLE users
ADD COLUMN created_at TIMESTAMP;

ALTER TABLE training_sessions
ADD COLUMN gym_room_id BIGINT;

ALTER TABLE enrollments
ADD COLUMN enrollment_call_type VARCHAR(255);